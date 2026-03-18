package com.example.loginframe.Service;

import com.example.loginframe.Entity.Feedback;
import com.example.loginframe.Repository.FeedbackRepository;
import com.example.loginframe.dto.FeedbackDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;

    public FeedbackDto saveFeedback(FeedbackDto dto) {
        Feedback feedback = Feedback.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .type(dto.getType())
                .rating(dto.getRating())
                .message(dto.getMessage())
                .build();

        Feedback saved = feedbackRepository.save(feedback);
        return mapToDto(saved);
    }

    public List<FeedbackDto> getAllFeedback() {
        return feedbackRepository.findAll()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private FeedbackDto mapToDto(Feedback feedback) {
        return FeedbackDto.builder()
                .id(feedback.getId())
                .name(feedback.getName())
                .email(feedback.getEmail())
                .type(feedback.getType())
                .rating(feedback.getRating())
                .message(feedback.getMessage())
                .build();
    }
}