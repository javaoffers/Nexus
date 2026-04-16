package model

type API struct {
	BaseModel
	SuiteID               int64  `gorm:"column:suite_id" json:"suiteId"`
	ApiCode               string `gorm:"column:api_code;size:100" json:"apiCode"`
	ApiProtocol           string `gorm:"column:api_protocol;size:10" json:"apiProtocol,omitempty"`
	ApiURL                string `gorm:"column:api_url;size:150" json:"apiUrl"`
	ApiName               string `gorm:"column:api_name;size:50" json:"apiName"`
	ApiDesc               string `gorm:"column:api_desc;size:200" json:"apiDesc,omitempty"`
	ApiRequestType        string `gorm:"column:api_request_type;size:10" json:"apiRequestType"`
	ApiRequestContentType string `gorm:"column:api_request_content_type;size:40" json:"apiRequestContentType,omitempty"`
}

func (API) TableName() string { return "t_api" }
