package com.fasttime.domain.resume.dto;

import com.fasttime.domain.resume.repository.ResumeOrderBy;

public record ResumesSearchRequest(
    // Order by(enum) (최신순(Default), 조회순, 좋아요 순)
    ResumeOrderBy orderBy,
    int page,
    int pageSize
) {

}
