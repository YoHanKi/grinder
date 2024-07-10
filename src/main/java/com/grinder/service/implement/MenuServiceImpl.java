package com.grinder.service.implement;

import com.grinder.domain.dto.MenuDTO;
import com.grinder.domain.entity.Cafe;
import com.grinder.domain.entity.Menu;
import com.grinder.domain.enums.ContentType;
import com.grinder.domain.enums.MenuType;
import com.grinder.repository.CafeRepository;
import com.grinder.repository.ImageRepository;
import com.grinder.repository.MenuRepository;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.grinder.repository.queries.MenuQueryRepository;
import com.grinder.service.ImageService;
import com.grinder.service.MenuService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MenuServiceImpl implements MenuService {
    private final MenuRepository menuRepository;
    private final AwsS3ServiceImpl s3Service;
    private final CafeRepository cafeRepository;
    private final ImageService imageService;

    public List<MenuDTO.findAllMenuResponse> findAllMenusByCafeId(String cafeId) {
        List<Menu> menuList = menuRepository.findAllByCafe_CafeId(cafeId);
        List<MenuDTO.findAllMenuResponse> dtoList = menuList.stream()
                .map(menu -> new MenuDTO.findAllMenuResponse(menu,imageService.findImageUrlByContentId(menu.getMenuId()))) // 변환 생성자 호출
                .collect(Collectors.toList());
        return dtoList;
    }
    @Override
    @Transactional
    public boolean saveMyCafeMenu(MenuDTO.saveMenuRequest request) {
        String uuid = UUID.randomUUID().toString();

        if (request.getMenuImage() != null) {
            s3Service.uploadSingleImageBucket(request.getMenuImage(), uuid, ContentType.MENU);
        }
        Cafe cafe = cafeRepository.findById(request.getCafeId()).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 카페 입니다."));

        boolean limit = request.getMenuIsLimited().equals("한정 메뉴");

        menuRepository.save(Menu.builder()
                .cafe(cafe)
                .menuId(uuid)
                .name(request.getMenuName())
                .menuType(MenuType.fromString(request.getMenuType()))
                .price(request.getMenuPrice())
                .volume(request.getMenuVolume())
                .allergy(request.getMenuAllergy())
                .details(request.getMenuDetails())
                .isLimited(limit).build());
        return true;
    }
}
