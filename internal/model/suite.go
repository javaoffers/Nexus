package model

type Suite struct {
	BaseModel
	SuiteCode       string `gorm:"column:suite_code;size:60" json:"suiteCode"`
	SuiteName       string `gorm:"column:suite_name;size:30" json:"suiteName"`
	SuiteClassifyID *int64 `gorm:"column:suite_classify_id" json:"suiteClassifyId,omitempty"`
	SuiteImage      string `gorm:"column:suite_image;type:text" json:"suiteImage,omitempty"`
	SuiteVersion    string `gorm:"column:suite_version;size:10" json:"suiteVersion,omitempty"`
	SuiteDesc       string `gorm:"column:suite_desc;size:140" json:"suiteDesc,omitempty"`
	SuiteHelpDocJSON string `gorm:"column:suite_help_doc_json;size:300" json:"suiteHelpDocJson,omitempty"`
	SuiteFlag       *int   `gorm:"column:suite_flag" json:"suiteFlag,omitempty"`
}

func (Suite) TableName() string { return "t_suite" }
