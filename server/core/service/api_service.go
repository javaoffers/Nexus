package service

import (
	"encoding/json"
	"fmt"
	"io"
	"net/http"
	"strings"
	"time"

	"github.com/nexus-flow/nexus/core/model"
	"github.com/nexus-flow/nexus/core/repository"
)

type APIInfoDTO struct {
	ID                    int64       `json:"id"`
	SuiteID               int64       `json:"suiteId"`
	SuiteFlag             *int        `json:"suiteFlag,omitempty"`
	ApiURL                string      `json:"apiUrl"`
	ApiName               string      `json:"apiName"`
	ApiDesc               string      `json:"apiDesc,omitempty"`
	ApiRequestType        string      `json:"apiRequestType"`
	ApiRequestContentType string      `json:"apiRequestContentType,omitempty"`
	ApiHeaders            interface{} `json:"apiHeaders,omitempty"`
	ApiInputParams        interface{} `json:"apiInputParams,omitempty"`
	ApiOutputParams       interface{} `json:"apiOutputParams,omitempty"`
}

func CreateAPI(a *model.API) error {
	return repository.CreateAPI(a)
}

func UpdateAPI(a *model.API) error {
	return repository.UpdateAPI(a)
}

func DeleteAPI(id int64) error {
	return repository.DeleteAPI(id)
}

func GetAPIInfo(id int64) (*APIInfoDTO, error) {
	a, err := repository.GetAPIByID(id)
	if err != nil {
		return nil, err
	}
	return buildAPIInfoDTO(a)
}

func GetAPIInfoByCode(code string) (*APIInfoDTO, error) {
	a, err := repository.GetAPIByCode(code)
	if err != nil {
		return nil, err
	}
	return buildAPIInfoDTO(a)
}

func buildAPIInfoDTO(a *model.API) (*APIInfoDTO, error) {
	dto := &APIInfoDTO{
		ID:                    a.ID,
		SuiteID:               a.SuiteID,
		ApiURL:                a.ApiURL,
		ApiName:               a.ApiName,
		ApiDesc:               a.ApiDesc,
		ApiRequestType:        a.ApiRequestType,
		ApiRequestContentType: a.ApiRequestContentType,
	}
	suite, err := repository.GetSuiteByID(a.SuiteID)
	if err == nil {
		dto.SuiteFlag = suite.SuiteFlag
	}

	// Load parameters
	headers, _ := repository.ListParametersBySourceAndType("api", a.ID, 4) // HEADER
	inputs, _ := repository.ListParametersBySourceAndType("api", a.ID, 1)  // INPUT
	outputs, _ := repository.ListParametersBySourceAndType("api", a.ID, 2) // OUTPUT
	dto.ApiHeaders = headers
	dto.ApiInputParams = inputs
	dto.ApiOutputParams = outputs
	return dto, nil
}

func ListAPIsBySuiteID(suiteID int64) ([]model.API, error) {
	return repository.ListAPIsBySuiteID(suiteID)
}

func ListAPIsBySuiteCode(suiteCode string) ([]model.API, error) {
	return repository.ListAPIsBySuiteCode(suiteCode)
}

func PageAPIs(p *model.PageParam, suiteID *int64, apiName, apiURL string) ([]model.API, int64, error) {
	return repository.PageAPIs(p, suiteID, apiName, apiURL)
}

func DebugAPI(apiID int64, headerData, inputParamData map[string]interface{}) (map[string]interface{}, error) {
	a, err := repository.GetAPIByID(apiID)
	if err != nil {
		return nil, fmt.Errorf("接口不存在")
	}

	url := a.ApiURL
	// Replace path variables
	for k, v := range inputParamData {
		placeholder := "{" + k + "}"
		if strings.Contains(url, placeholder) {
			url = strings.ReplaceAll(url, placeholder, fmt.Sprintf("%v", v))
			delete(inputParamData, k)
		}
	}

	client := &http.Client{Timeout: 30 * time.Second}
	var req *http.Request

	switch strings.ToUpper(a.ApiRequestType) {
	case "GET":
		req, _ = http.NewRequest("GET", url, nil)
		q := req.URL.Query()
		for k, v := range inputParamData {
			q.Set(k, fmt.Sprintf("%v", v))
		}
		req.URL.RawQuery = q.Encode()
	default:
		body, _ := json.Marshal(inputParamData)
		req, _ = http.NewRequest(strings.ToUpper(a.ApiRequestType), url, strings.NewReader(string(body)))
		ct := a.ApiRequestContentType
		if ct == "" {
			ct = "application/json"
		}
		req.Header.Set("Content-Type", ct)
	}

	for k, v := range headerData {
		req.Header.Set(k, fmt.Sprintf("%v", v))
	}

	resp, err := client.Do(req)
	if err != nil {
		return nil, fmt.Errorf("请求失败: %v", err)
	}
	defer resp.Body.Close()

	respBody, _ := io.ReadAll(resp.Body)
	var result map[string]interface{}
	if json.Unmarshal(respBody, &result) != nil {
		result = map[string]interface{}{"response": string(respBody)}
	}
	return result, nil
}

func SaveAPIParameters(apiID int64, sourceType string, headers, inputs, outputs []model.Parameter) error {
	repository.DeleteParametersBySource(sourceType, apiID)

	for i := range headers {
		headers[i].SourceType = sourceType
		headers[i].SourceID = &apiID
		pt := 4
		headers[i].ParamType = &pt
	}
	for i := range inputs {
		inputs[i].SourceType = sourceType
		inputs[i].SourceID = &apiID
		pt := 1
		inputs[i].ParamType = &pt
	}
	for i := range outputs {
		outputs[i].SourceType = sourceType
		outputs[i].SourceID = &apiID
		pt := 2
		outputs[i].ParamType = &pt
	}

	all := append(append(headers, inputs...), outputs...)
	return repository.BatchCreateParameters(all)
}
