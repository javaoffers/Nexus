package db

import (
	"fmt"
	"os"
	"path/filepath"

	"github.com/nexus-flow/nexus/config"
	"github.com/nexus-flow/nexus/core/model"
	"github.com/nexus-flow/nexus/pkg/crypto"
	"gorm.io/driver/mysql"
	"gorm.io/driver/sqlite"
	"gorm.io/gorm"
	"gorm.io/gorm/logger"
)

var DB *gorm.DB

func Init() error {
	var dialector gorm.Dialector
	cfg := config.C.Database

	switch cfg.Driver {
	case "mysql":
		dsn := fmt.Sprintf("%s:%s@tcp(%s:%d)/%s?charset=utf8mb4&parseTime=True&loc=Asia%%2FShanghai",
			cfg.MySQL.Username, cfg.MySQL.Password, cfg.MySQL.Host, cfg.MySQL.Port, cfg.MySQL.Database)
		dialector = mysql.Open(dsn)
	default:
		dbPath := cfg.SQLite.Path
		if err := os.MkdirAll(filepath.Dir(dbPath), 0755); err != nil {
			return err
		}
		dialector = sqlite.Open(dbPath)
	}

	var err error
	DB, err = gorm.Open(dialector, &gorm.Config{
		Logger: logger.Default.LogMode(logger.Warn),
	})
	if err != nil {
		return fmt.Errorf("open database: %w", err)
	}

	sqlDB, err := DB.DB()
	if err != nil {
		return err
	}
	sqlDB.SetMaxOpenConns(100)
	sqlDB.SetMaxIdleConns(10)

	if err := autoMigrate(); err != nil {
		return fmt.Errorf("auto migrate: %w", err)
	}

	seedData()
	return nil
}

func autoMigrate() error {
	return DB.AutoMigrate(
		&model.User{},
		&model.Suite{},
		&model.API{},
		&model.Object{},
		&model.Parameter{},
		&model.FlowDefinition{},
		&model.FlowInfo{},
		&model.FlowVersion{},
		&model.VariableInfo{},
		&model.Token{},
		&model.DataSource{},
		&model.FlowTemplate{},
	)
}

func seedData() {
	var count int64
	DB.Model(&model.User{}).Count(&count)
	if count == 0 {
		DB.Create(&model.User{
			BaseModel: model.BaseModel{ID: 1},
			UserName:  "nexus",
			Password:  crypto.MD5("nexus123"),
		})
	}
}
