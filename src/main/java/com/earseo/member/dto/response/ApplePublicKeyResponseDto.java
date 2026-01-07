package com.earseo.member.dto.response;

import java.util.List;

public record ApplePublicKeyResponseDto(
        List<ApplePublicKey> keys
) {
    public record ApplePublicKey(
            String kty,
            String kid,
            String use,
            String alg,
            String n,
            String e
    ) {
    }
}
