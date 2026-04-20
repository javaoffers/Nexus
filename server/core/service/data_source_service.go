package service

import (
	"database/sql"
	"fmt"
	"time"

	_ "github.com/go-sql-driver/mysql"
	"github.com/nexus-flow/nexus/core/model"
	"github.com/nexus-flow/nexus/core/repository"
)

func CreateDataSource(d *model.DataSource) error {
	return repository.CreateDataSource(d)
}

func UpdateDataSource(d *model.DataSource) error {
	return repository.UpdateDataSource(d)
}

func DeleteDataSource(id int64) error {
	return repository.DeleteDataSource(id)
}

func GetDataSourceByID(id int64) (*model.DataSource, error) {
	return repository.GetDataSourceByID(id)
}

func ListDataSources() ([]model.DataSource, error) {
	return repository.ListDataSources()
}

func PageDataSources(p *model.PageParam, name, dsType string) ([]model.DataSource, int64, error) {
	return repository.PageDataSources(p, name, dsType)
}

func TestDataSourceConnection(id int64) error {
	ds, err := repository.GetDataSourceByID(id)
	if err != nil {
		return fmt.Errorf("数据源不存在")
	}

	dsn := fmt.Sprintf("%s:%s@tcp(%s:%s)/%s?timeout=5s",
		ds.UserName, ds.Password, ds.Address, ds.Port, ds.DatabaseName)

	db, err := sql.Open("mysql", dsn)
	if err != nil {
		return fmt.Errorf("连接失败: %v", err)
	}
	defer db.Close()

	db.SetConnMaxLifetime(5 * time.Second)
	return db.Ping()
}
