package com.branding.branding_backend.branding.service;

import com.branding.branding_backend.user.User;

import java.util.Map;

public interface InterviewService {

    Map<String, Object> processInterview(
            Long userId,
            Map<String, Object> interviewInput
    );
}
