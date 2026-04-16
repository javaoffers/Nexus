package service

import (
	"errors"

	"github.com/nexus-flow/nexus/internal/model"
	"github.com/nexus-flow/nexus/internal/repository"
)

func CreateSuite(s *model.Suite) error {
	return repository.CreateSuite(s)
}

func UpdateSuite(s *model.Suite) error {
	return repository.UpdateSuite(s)
}

func DeleteSuite(id int64) error {
	count, _ := repository.CountAPIsBySuiteID(id)
	if count > 0 {
		return errors.New("该套件下存在接口，无法删除")
	}
	return repository.DeleteSuite(id)
}

func ListSuites() ([]model.Suite, error) {
	return repository.ListSuites()
}

func PageSuites(p *model.PageParam, suiteName string, suiteFlag *int) ([]model.Suite, int64, error) {
	return repository.PageSuites(p, suiteName, suiteFlag)
}
