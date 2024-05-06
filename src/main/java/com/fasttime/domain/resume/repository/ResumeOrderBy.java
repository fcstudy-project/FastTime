package com.fasttime.domain.resume.repository;

public enum ResumeOrderBy {
    DATE,
    VIEW_COUNT,
    LIKE_COUNT;

    public static ResumeOrderBy of(String value) {
        return switch (value.toLowerCase()) {
            case "viewcount" -> ResumeOrderBy.VIEW_COUNT;
            case "likecount" -> ResumeOrderBy.LIKE_COUNT;
            default -> ResumeOrderBy.DATE;
        };
    }


}
