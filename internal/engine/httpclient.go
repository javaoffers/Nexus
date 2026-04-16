package engine

import (
	"encoding/json"
	"fmt"
	"io"
	"net/http"
	"strings"
	"time"
)

var httpClient = &http.Client{
	Timeout: 30 * time.Second,
	Transport: &http.Transport{
		MaxIdleConns:        200,
		MaxIdleConnsPerHost: 80,
		IdleConnTimeout:     90 * time.Second,
	},
}

// SendHTTPRequest sends an HTTP request and returns the parsed response.
func SendHTTPRequest(method MethodDef, headers, params map[string]interface{}) (map[string]interface{}, error) {
	url := method.URL

	// Replace path variables
	for k, v := range params {
		placeholder := "{" + k + "}"
		if strings.Contains(url, placeholder) {
			url = strings.ReplaceAll(url, placeholder, fmt.Sprintf("%v", v))
			delete(params, k)
		}
	}

	var req *http.Request
	var err error

	switch strings.ToUpper(method.RequestType) {
	case "GET":
		req, err = http.NewRequest("GET", url, nil)
		if err != nil {
			return nil, err
		}
		q := req.URL.Query()
		for k, v := range params {
			q.Set(k, fmt.Sprintf("%v", v))
		}
		req.URL.RawQuery = q.Encode()
	default:
		body, _ := json.Marshal(params)
		req, err = http.NewRequest(strings.ToUpper(method.RequestType), url, strings.NewReader(string(body)))
		if err != nil {
			return nil, err
		}
		ct := method.RequestContentType
		if ct == "" {
			ct = "application/json"
		}
		req.Header.Set("Content-Type", ct)
	}

	// Set custom headers
	for k, v := range headers {
		req.Header.Set(k, fmt.Sprintf("%v", v))
	}

	resp, err := httpClient.Do(req)
	if err != nil {
		return nil, fmt.Errorf("HTTP request failed: %w", err)
	}
	defer resp.Body.Close()

	respBody, err := io.ReadAll(resp.Body)
	if err != nil {
		return nil, fmt.Errorf("read response: %w", err)
	}

	var result map[string]interface{}
	if json.Unmarshal(respBody, &result) != nil {
		result = map[string]interface{}{"response": string(respBody)}
	}
	return result, nil
}
