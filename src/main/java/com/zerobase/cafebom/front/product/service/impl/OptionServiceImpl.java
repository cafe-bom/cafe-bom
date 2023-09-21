package com.zerobase.cafebom.front.product.service.impl;

import com.zerobase.cafebom.common.exception.CustomException;
import com.zerobase.cafebom.front.product.domain.Option;
import com.zerobase.cafebom.front.product.domain.OptionRepository;
import com.zerobase.cafebom.front.product.dto.OptionDto;
import com.zerobase.cafebom.front.product.dto.OptionForm;
import com.zerobase.cafebom.front.product.domain.OptionCategory;
import com.zerobase.cafebom.front.product.domain.OptionCategoryRepository;
import com.zerobase.cafebom.front.product.service.OptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.zerobase.cafebom.common.exception.ErrorCode.OPTION_CATEGORY_NOT_EXISTS;
import static com.zerobase.cafebom.common.exception.ErrorCode.OPTION_NOT_EXISTS;

@Service
@RequiredArgsConstructor
public class OptionServiceImpl implements OptionService {

    private final OptionRepository optionRepository;
    private final OptionCategoryRepository optionCategoryRepository;

    // 옵션 등록-jiyeon-23.08.30
    @Override
    public void addOption(OptionDto.Request optionAddDto) {
        Integer optionCategoryId = optionAddDto.getOptionCategoryId();
        OptionCategory optionCategory = optionCategoryRepository.findById(optionCategoryId)
                .orElseThrow(() -> new CustomException(OPTION_NOT_EXISTS));
        Option option = Option.builder()
                .optionCategory(optionCategory)
                .name(optionAddDto.getName())
                .price(optionAddDto.getPrice())
                .build();
        optionRepository.save(option);
    }

    // 옵션 수정-jiyeon-23.08.30
    @Override
    public void modifyOption(Integer id, OptionDto.Request request) {
        Integer optionCategoryId = request.getOptionCategoryId();
        OptionCategory optionCategory = optionCategoryRepository.findById(optionCategoryId)
                .orElseThrow(() -> new CustomException(OPTION_CATEGORY_NOT_EXISTS));

        Option optionId = optionRepository.findById(id)
                .orElseThrow(() -> new CustomException(OPTION_NOT_EXISTS));

        Option modifyOption = Option.builder()
                .optionCategory(optionCategory)
                .name(request.getName())
                .price(request.getPrice())
                .build();

        optionRepository.save(modifyOption.toBuilder()
                .id(optionId.getId())
                .build());

    }

    // 옵션 삭제-jiyeon-23.08.30
    @Override
    public void removeOption(Integer id) {
        Option option = optionRepository.findById(id)
                .orElseThrow(() -> new CustomException(OPTION_NOT_EXISTS));
        optionRepository.deleteById(option.getId());
    }

    // 옵션 전체 조회-jiyeon-23.08.30
    @Override
    public List<OptionForm.Response> findAllOption() {
        List<Option> optionList = optionRepository.findAll();
        List<OptionForm.Response> optionDtoList = optionList.stream()
                .map(OptionForm.Response::from)
                .collect(Collectors.toList());
        return optionDtoList;
    }

    // 옵션Id별 조회-jiyeon-23.08.30
    @Override
    public OptionForm.Response findByIdOption(Integer id) {
        Option option = optionRepository.findById(id)
                .orElseThrow(() -> new CustomException(OPTION_NOT_EXISTS));
        OptionForm.Response response = OptionForm.Response.from(option);
        return response;
    }
}