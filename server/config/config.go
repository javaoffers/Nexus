package config

import (
	"github.com/spf13/viper"
)

type Config struct {
	Server   ServerConfig   `mapstructure:"server"`
	Database DatabaseConfig `mapstructure:"database"`
	Auth     AuthConfig     `mapstructure:"auth"`
	Cache    CacheConfig    `mapstructure:"cache"`
	Nexus    NexusConfig    `mapstructure:"nexus"`
}

type ServerConfig struct {
	Port int `mapstructure:"port"`
}

type DatabaseConfig struct {
	Driver string       `mapstructure:"driver"`
	SQLite SQLiteConfig `mapstructure:"sqlite"`
	MySQL  MySQLConfig  `mapstructure:"mysql"`
}

type SQLiteConfig struct {
	Path string `mapstructure:"path"`
}

type MySQLConfig struct {
	Host     string `mapstructure:"host"`
	Port     int    `mapstructure:"port"`
	Database string `mapstructure:"database"`
	Username string `mapstructure:"username"`
	Password string `mapstructure:"password"`
}

type AuthConfig struct {
	JWTSecret string `mapstructure:"jwt-secret"`
	JWTExpire int    `mapstructure:"jwt-expire"`
}

type CacheConfig struct {
	Type  string      `mapstructure:"type"`
	Redis RedisConfig `mapstructure:"redis"`
}

type RedisConfig struct {
	Addr     string `mapstructure:"addr"`
	Password string `mapstructure:"password"`
}

type NexusConfig struct {
	APIKey         string `mapstructure:"api-key"`
	OpenServerAddr string `mapstructure:"open-server-addr"`
}

var C Config

func Load() error {
	viper.SetConfigName("config")
	viper.SetConfigType("yaml")
	viper.AddConfigPath(".")
	viper.AddConfigPath("./config")

	// defaults
	viper.SetDefault("server.port", 9127)
	viper.SetDefault("database.driver", "sqlite")
	viper.SetDefault("database.sqlite.path", "./data/nexus.db")
	viper.SetDefault("auth.jwt-secret", "www.javaoffers.com####www.nexus.plus")
	viper.SetDefault("auth.jwt-expire", 7200)
	viper.SetDefault("cache.type", "memory")
	viper.SetDefault("nexus.open-server-addr", "https://open.nexus.plus")

	if err := viper.ReadInConfig(); err != nil {
		if _, ok := err.(viper.ConfigFileNotFoundError); !ok {
			return err
		}
	}

	return viper.Unmarshal(&C)
}
