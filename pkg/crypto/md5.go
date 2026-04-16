package crypto

import (
	"crypto/md5"
	"encoding/hex"
)

func MD5(text string) string {
	h := md5.Sum([]byte(text))
	return hex.EncodeToString(h[:])
}
