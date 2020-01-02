package main

import (
        "fmt"
        "context"
		"github.com/aws/aws-lambda-go/lambda"
		"crypto"
		"crypto/rsa"
		"crypto/sha1"
		"crypto/x509"
		"encoding/hex"
		"encoding/pem"
		"io/ioutil"
		"log"
)

type VerifyMessage struct {
		Text string `json:"text"`
		Sig  string `json:"signature"`
}

func verify(pubkey *rsa.PublicKey, sig string, message string) (bool,error) {
	msg := []byte(message)
	signature, err1 := hex.DecodeString(sig)
	if err1 != nil {
		return false, fmt.Errorf("Error decoding signature")
	}
	returnValue := true
	hashed := sha1.Sum(msg)

	err := rsa.VerifyPKCS1v15(pubkey, crypto.SHA1, hashed[:], signature)
	if err != nil {
		returnValue = false
	}
	return returnValue, nil
}

func loadKey(filepath string) (*rsa.PublicKey,error) {
	pub, err := ioutil.ReadFile(filepath)
	if err != nil {
		return nil, fmt.Errorf("Cannot open pem file")
	}
	pubPem, _ := pem.Decode(pub)
	parsedKey, err := x509.ParsePKIXPublicKey(pubPem.Bytes) 
	if err != nil {
		return nil, fmt.Errorf("Error parsing key")
	}

	var pubKey *rsa.PublicKey
	pubKey, ok := parsedKey.(*rsa.PublicKey)
	if !ok {
		return nil, fmt.Errorf("Cannot parse public key")
	}
	return pubKey,nil
}

func HandleRequest(ctx context.Context, msg VerifyMessage) (bool, error) {
	key, err1 := loadKey("./public.pem")
	if err1 != nil {
		log.Fatal("Error loading public key")
	}
	return verify(key, msg.Sig, msg.Text)
}

func main() {
        lambda.Start(HandleRequest)
}