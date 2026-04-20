package auth

import (
	"time"

	"github.com/golang-jwt/jwt/v5"
	"github.com/nexus-flow/nexus/config"
)

type Claims struct {
	UserID int64 `json:"userId"`
	jwt.RegisteredClaims
}

func GenerateToken(userID int64) (string, error) {
	expire := time.Duration(config.C.Auth.JWTExpire) * time.Second
	claims := Claims{
		UserID: userID,
		RegisteredClaims: jwt.RegisteredClaims{
			ExpiresAt: jwt.NewNumericDate(time.Now().Add(expire)),
			Issuer:    "nexus",
			IssuedAt:  jwt.NewNumericDate(time.Now()),
		},
	}
	token := jwt.NewWithClaims(jwt.SigningMethodHS256, claims)
	return token.SignedString([]byte(config.C.Auth.JWTSecret))
}

func ParseToken(tokenStr string) (*Claims, error) {
	token, err := jwt.ParseWithClaims(tokenStr, &Claims{}, func(t *jwt.Token) (interface{}, error) {
		return []byte(config.C.Auth.JWTSecret), nil
	})
	if err != nil {
		return nil, err
	}
	if claims, ok := token.Claims.(*Claims); ok && token.Valid {
		return claims, nil
	}
	return nil, jwt.ErrTokenInvalidClaims
}
