package service

import (
	"errors"

	"github.com/nexus-flow/nexus/core/repository"
	"github.com/nexus-flow/nexus/pkg/auth"
)

type LoginResult struct {
	UserName string `json:"userName"`
	Token    string `json:"token"`
}

type UserInfo struct {
	ID       int64  `json:"id"`
	UserName string `json:"userName"`
}

func Login(userName, password string) (*LoginResult, error) {
	user, err := repository.GetUserByName(userName)
	if err != nil {
		return nil, errors.New("用户名或密码错误")
	}
	if password != user.Password {
		return nil, errors.New("用户名或密码错误")
	}
	token, err := auth.GenerateToken(user.ID)
	if err != nil {
		return nil, errors.New("生成Token失败")
	}
	return &LoginResult{
		UserName: user.UserName,
		Token:    token,
	}, nil
}

func GetUserInfo(userID int64) (*UserInfo, error) {
	user, err := repository.GetUserByID(userID)
	if err != nil {
		return nil, errors.New("用户不存在")
	}
	return &UserInfo{
		ID:       user.ID,
		UserName: user.UserName,
	}, nil
}

func UpdatePassword(userID int64, oldPwd, newPwd string) error {
	user, err := repository.GetUserByID(userID)
	if err != nil {
		return errors.New("用户不存在")
	}
	if oldPwd != user.Password {
		return errors.New("原密码错误")
	}
	return repository.UpdateUserPassword(userID, newPwd)
}
