package com.grinder.service.implement;

import com.grinder.domain.dto.MemberDTO;
import com.grinder.domain.entity.Image;
import com.grinder.domain.entity.Member;
import com.grinder.domain.enums.ContentType;
import com.grinder.domain.enums.Role;
import com.grinder.repository.ImageRepository;
import com.grinder.repository.MemberRepository;
import com.grinder.repository.queries.MemberQueryRepository;
import com.grinder.repository.queries.SearchQueryRepository;
import com.grinder.service.MailService;
import com.grinder.service.MemberService;
import com.grinder.utils.RedisUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.SliceImpl;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;

import static com.grinder.domain.dto.MemberDTO.*;


@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final ImageRepository imageRepository;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final MemberQueryRepository memberQueryRepository;
    private final MailService mailService;
    private final RedisUtil redisUtil;
    private final SearchQueryRepository searchQueryRepository;

    public Member findMemberById(String memberId) {
        return memberRepository.findById(memberId).orElseThrow(() -> new NoSuchElementException("회원 아이디: " + memberId + " 인 회원이 존재하지 않습니다."));
    }

    @Override
    public Member findMemberByEmail(String memberEmail) {
        return memberRepository.findByEmail(memberEmail).orElseThrow(() -> new NoSuchElementException("회원 아이디" + memberEmail + "인 회원이 존재하지 않습니다."));
    }

    @Transactional
    @Override
    public MemberDTO.FindMemberAndImageDTO findMemberAndImageById(String memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new NoSuchElementException("회원 아이디: " + memberId + " 인 회원이 존재하지 않습니다."));
        String image = imageRepository.findByContentTypeAndContentId(ContentType.MEMBER, memberId).orElse(new Image()).getImageUrl();
        return new MemberDTO.FindMemberAndImageDTO(member, image);
    }

//    @Transactional
//    @Override
//    public MemberDTO.FindMemberAndImageDTO findMemberAndImageByEmail(String email) {
//        Member member = memberRepository.findByEmail(email).orElseThrow(() -> new NoSuchElementException("회원 아이디: " + email + " 인 회원이 존재하지 않습니다."));
//        String image = imageRepository.findByContentTypeAndContentId(ContentType.MEMBER, member.getMemberId()).orElse(new Image()).getImageUrl();
//        return new MemberDTO.FindMemberAndImageDTO(member, image);
//    }

    @Override
    @Transactional
    public void updateMemberRole(String memberId) {
        Member member = findMemberById(memberId);

        if (member.getRole() == Role.MEMBER) {
            member.verify();
        } else if (member.getRole() == Role.VERIFIED_MEMBER) {
            member.cancelVerify();
        }
    }

    @Override
    @Transactional
    public boolean deleteMember(String memberId) {
        Member member = findMemberById(memberId);

        return member.delete();
    }

    @Override
    @Transactional
    public boolean recoverMember(String memberId) {
        Member member = findMemberById(memberId);
        return member.recover();
    }

    @Override
    public Slice<FindMemberDTO> searchMemberSlice(String role, String nickname, Pageable pageable) {
        return memberQueryRepository.searchMemberByRoleAndNicknameSlice(role, nickname, pageable);
    }

    @Override
    public boolean addMember(MemberRequestDto request){
        Member member = Member.builder()
                .email(request.getEmail())
                .nickname(request.getNickname())
                .password(passwordEncoder.encode(request.getPassword()))
                .phoneNum(request.getPhoneNum())
                .build();
        memberRepository.save(member);
        return true;
    }

    @Override
    @Transactional
    public boolean updateMember(MemberDTO.MemberUpdateRequestDto request) {
        Member member = memberRepository.findById(request.getMemberId()).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 회원입니다."));
        if (!passwordEncoder.matches(request.getNowPassword(), member.getPassword())) {
            throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
        }
        memberRepository.updateMemberInfo(request.getMemberId()
        ,request.getNickname()
        ,passwordEncoder.encode(request.getPassword())
        ,request.getPhoneNum());
        return true;
    }

    @Override
    public boolean checkEmail(String email){
        return memberRepository.existsByEmail(email);
    }

    @Override
    public boolean checkNickname(String nickname){
        return memberRepository.existsByNickname(nickname);
    }

    public boolean sendCodeToEmail(String toEmail) {
        String title = "이메일 인증 번호";
        String authCode = this.createCode();
        String content =
                "CAFE GRINDER에 방문해주셔서 감사합니다." + 	//html 형식으로 작성 !
                        "<br><br>" +
                        "인증 번호는 " + authCode + "입니다." +
                        "<br>" +
                        "인증번호를 웹사이트에 입력해주세요";
        mailService.sendEmail(toEmail, title, content);
        // 이메일 인증 요청 시 인증 번호 Redis에 저장 ( key = "AuthCode " + Email / value = AuthCode )
        redisUtil.set(toEmail,
                authCode, 30);
        return true;
    }

    private String createCode() {
        int lenth = 6;
        try {
            Random random = SecureRandom.getInstanceStrong();
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < lenth; i++) {
                builder.append(random.nextInt(10));
            }
            return builder.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException("번호 생성에 실패했습니다.");
        }
    }

    @Override
    public boolean verifiedCode(String email, String authCode) {
        String redisAuthCode = (String) redisUtil.get(email);

        return authCode.equals(redisAuthCode);
    }

    @Override
    public boolean changePassword(String email){
        String password = this.createCode();
        String title = "비밀번호 변경 안내";
        String content =
                "CAFE GRINDER에 방문해주셔서 감사합니다." + 	//html 형식으로 작성 !
                        "<br><br>" +
                        "변경된 비밀번호는 " + password + "입니다." +
                        "<br>" +
                        "추후 비밀번호 변경을 부탁드립니다.";
        Member member = findMemberByEmail(email);
        member.setPassword(passwordEncoder.encode(password));
        mailService.sendEmail(email, title, content);
        return true;
    }

    @Override
    public boolean existEmail(String email) {
        return memberRepository.existsByEmail(email);
    }

    @Override
    public Slice<MemberDTO.SearchMemberDTO> searchMember(String memberId ,String query, Pageable pageable) {
        Slice<FindMemberAndImageDTO> findMemberDto = searchQueryRepository.searchMembersByNicknameOrEmail(query, pageable);
        return new SliceImpl<>(findMemberDto.getContent().stream().map(dto -> new SearchMemberDTO(memberId, dto)).toList(), pageable, findMemberDto.hasNext());
    }
}
