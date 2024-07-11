package com.grinder.service.implement;

import com.grinder.domain.entity.AnalysisTag;
import com.grinder.domain.entity.Member;
import com.grinder.exception.RecentAddedTagException;
import com.grinder.repository.AnalysisTagRepository;
import com.grinder.repository.MemberRepository;
import com.grinder.repository.queries.AnalysisTagQueryRepository;
import com.grinder.service.AnalysisTagService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static java.time.LocalDate.now;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnalysisTagServiceImpl implements AnalysisTagService {
    private final AnalysisTagRepository analysisTagRepository;
    private final AnalysisTagQueryRepository analysisTagQueryRepository;
    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public void updateTagList(String email) {
        AnalysisTag analysisTag = analysisTagRepository.findByMember_Email(email)
                .orElseThrow(() -> new EntityNotFoundException("해당 회원이 존재하지 않습니다."));

        List<String> list = analysisTagQueryRepository.AnalysisMemberTag(email);
        for (String str : list) {
            if (!list.isEmpty()) {
                analysisTag.addTags(str);
                analysisTagRepository.save(analysisTag);
            }
        }
    }

    @Override
    @Transactional
    public AnalysisTag findByEmail(String email) {
        AnalysisTag analysisTag = null;
        try {
            analysisTag = analysisTagRepository.findByMember_Email(email).orElseThrow(() -> new EntityNotFoundException("해당 회원이 존재하지 않습니다."));
            LocalDateTime now = LocalDateTime.now();
            Duration duration = Duration.between(analysisTag.getUpdatedAt(), now);
            if (duration.toDays() < 3) {
                throw new RecentAddedTagException("최근 태그가 추가 되었습니다. 다음에 다시 추가해주세요!");
            }
        } catch (EntityNotFoundException e) {
            Member member = memberRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
            analysisTag = analysisTagRepository.save(AnalysisTag.builder().member(member).tagList(now() + "").build());
        }
        return analysisTag;
    }

    @Override
    @Transactional
    public boolean addTagList(List<String> list, AnalysisTag analysisTag) {
        try {
            for (String item : list) {
                analysisTag.addTags(item);
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
