package br.com.limmazk.spring_boot_essentials.dto;

public record TokenResponseDto(String token, long expiresIn) {
}
