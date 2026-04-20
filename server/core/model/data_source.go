package model

type DataSource struct {
	BaseModel
	DataSourceName    string `gorm:"column:data_source_name;size:150" json:"dataSourceName"`
	DataSourceType    string `gorm:"column:data_source_type;size:20" json:"dataSourceType"`
	DataSourceDesc    string `gorm:"column:data_source_desc;size:200" json:"dataSourceDesc,omitempty"`
	Address           string `gorm:"column:address;size:200" json:"address"`
	Port              string `gorm:"column:port;size:10" json:"port"`
	UserName          string `gorm:"column:user_name;size:40" json:"userName"`
	Password          string `gorm:"column:password;size:40" json:"password"`
	DatabaseName      string `gorm:"column:database_name;size:40" json:"databaseName"`
	ConnectExtInfo    string `gorm:"column:connect_ext_info;size:200" json:"connectExtInfo,omitempty"`
	MinPoolSize       *int   `gorm:"column:min_pool_size" json:"minPoolSize,omitempty"`
	MaxPoolSize       *int   `gorm:"column:max_pool_size" json:"maxPoolSize,omitempty"`
	QueryTimeout      *int   `gorm:"column:query_timeout" json:"queryTimeout,omitempty"`
	DataSourceExtInfo string `gorm:"column:data_source_ext_info;size:200" json:"dataSourceExtInfo,omitempty"`
}

func (DataSource) TableName() string { return "t_data_source" }
