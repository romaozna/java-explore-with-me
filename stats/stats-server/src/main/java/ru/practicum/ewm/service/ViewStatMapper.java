package ru.practicum.ewm.service;

import ru.practicum.ewm.dto.ViewStatDto;
import ru.practicum.ewm.model.ViewStat;

import java.util.List;
import java.util.stream.Collectors;


public class ViewStatMapper {
    public static List<ViewStatDto> toViewStatDto(List<ViewStat> viewStats) {
        return viewStats
                .stream()
                .map(viewStat -> new ViewStatDto(viewStat.getApp(), viewStat.getUri(), viewStat.getHits()))
                .collect(Collectors.toList());
    }
}
