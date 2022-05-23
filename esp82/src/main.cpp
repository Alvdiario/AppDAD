#include <Arduino.h>
#define ARDUINOJSON_USE_LONG_LONG 1
#include "ArduinoJson.h"
#include <ESP8266WiFi.h>
#include <ESP8266HTTPClient.h>
#include <SoftwareSerial.h>
#include <I2Cdev.h>
#include <MPU6050.h>
#include <Wire.h>
#include <TinyGPS.h>
#include <iostream>
#include <string>
#include <sstream>
char responseBuffer[300];
WiFiClient client;

String SSID = "wifi";
String PASS = "pass";

String SERVER_IP ="192.168.1.45";
int SERVER_PORT = 8081;
const int mpuAddress = 0x68;
MPU6050 mpu(mpuAddress);

int16_t ax, ay, az;
int axi,ayi,azi=0;
int16_t gx, gy, gz;
int gxi,gyi,gzi=0;
void sendGetValueBySensor();
void PutValueBySensor(int,int,int,int,int,int);
void PostValueBySensor(int,int,int,int,int,int);
void PutValueBySensor();
//usuario
void sendGetUsuario();
void putUsuario();
void postUsuarioActualiza();
void deleteUsuario();

//dispositivo
void getDispositivo();
void putDispositivo();
void postDispositivaActualiza();
//
void sendPostRequest();
//gps
TinyGPS gps;

SoftwareSerial softSerial(2, 0);
void getGps();
void postGps(String);
void putGps(String);
String coord="LAT=36 LON=-5";
bool inicio = true;



void setup() {
  Serial.begin(115200);
  softSerial.begin(9600);

  WiFi.begin(SSID, PASS);
  Wire.begin();
mpu.initialize();
Serial.println(mpu.testConnection() ? F("IMU iniciado correctamente") : F("Error al iniciar IMU"));

  Serial.print("Connecting...");
  while (WiFi.status() != WL_CONNECTED){
    delay(500);
    Serial.print(".");
  }
  Serial.print("Connected, IP address: ");
  Serial.print(WiFi.localIP());
}

void loop() {
  mpu.getAcceleration(&ax, &ay, &az);
  mpu.getRotation(&gx, &gy, &gz);
  bool newData = false;
//  float accel_ang_x = atan(ax / sqrt(pow(ay, 2) + pow(az, 2)))*(180.0 / 3.14);---->calculamos en telegramvertx
//	float accel_ang_y = atan(ay / sqrt(pow(ax, 2) + pow(az, 2)))*(180.0 / 3.14);

                      int axi=ax;
                      int ayi=ay;
                      int azi=az;
                      int gxi=gx;
                      int gyi=gy;
                      int gzi=gz;

PostValueBySensor(gxi,gyi,gzi,axi,ayi,azi);

	delay(1000);

for (unsigned long start = millis(); millis() - start < 1000;)
{
  while (softSerial.available())
  {
    char c = softSerial.read();
    if (gps.encode(c)) // Nueva secuencia recibida
      newData = true;
  }
}

if (newData)
{
  float flat, flon;
  unsigned long age;
  gps.f_get_position(&flat, &flon, &age);






String  coord ="LAT=" +String(flat)+" LON="+String(flon);
  Serial.print(coord);
    postGps(coord);

  delay(5000);
}
if (inicio){
  putGps(coord);
  inicio=false;
}
delay(3000);



  }

  void sendGetValueBySensor(){
    if (WiFi.status() == WL_CONNECTED){
      HTTPClient http;
      http.begin(client, SERVER_IP, SERVER_PORT, "/v3/7e72bb8b-4a7d-4089-9d66-053dea732c03", true);//"/api/usuario/:idusuario"
      int httpCode = http.GET();

      Serial.println("Response code: " + httpCode);

      String payload = http.getString();

      const size_t capacity = JSON_OBJECT_SIZE(9) + JSON_ARRAY_SIZE(10) + 60;
      DynamicJsonDocument doc(capacity);

      DeserializationError error = deserializeJson(doc, payload);
      if (error){
        Serial.print("deserializeJson() failed: ");
        Serial.println(error.c_str());
        return;
      }
      Serial.println(F("Response:"));

      int idmpu=doc["idsensor_valor_mpu6050"].as<int>();
      int id=doc["idsensor"].as<int>();
      int avaluex=doc["a_value_x"].as<int>();
      int avaluey=doc["a_value_y"].as<int>();
      int avaluez=doc["a_value_z"].as<int>();
      int gvaluex=doc["g_value_x"].as<int>();
      int gvaluey=doc["g_value_y"].as<int>();
      int gvaluez=doc["g_value_z"].as<int>();
      long time=doc["timesstamp"].as<long>();

        Serial.println("Idmpu: " + String(idmpu));
        Serial.println("Id: " + String(id));
        Serial.println("Avaluex: " + String(avaluex));
        Serial.println("Avaluey: " + String(avaluey));
        Serial.println("Avaluez: " + String(avaluez));
        Serial.println("Gvaluex: " + String(gvaluex));
        Serial.println("Gvaluey: " + String(gvaluey));
        Serial.println("Gvaluez: " + String(gvaluez));
        Serial.println("Time: " + String(time));
        Serial.setTimeout(10000);
    }
  }
  void PutValueBySensor(){
    if (WiFi.status() == WL_CONNECTED){
      HTTPClient http;
      http.begin(client, SERVER_IP, SERVER_PORT, "/api/mpu6050", true);
      http.addHeader("Content-Type", "application/json");

      const size_t capacity = JSON_OBJECT_SIZE(9) + JSON_ARRAY_SIZE(2) + 600;
      DynamicJsonDocument doc(capacity);
      doc["idsensor_valor_mpu6050"] = 3;
      doc["idsensor"] = 1;
      doc["a_value_x"] = 0;
      doc["a_value_y"] = 0;
      doc["a_value_z"] = 0;
      doc["g_value_x"] = 0;
        doc["g_value_y"] = 0;
          doc["g_value_z"] = 0;
            doc["timesstamp"] = 1235646879546;

      String output;
      serializeJson(doc, output);

      int httpCode = http.PUT(output);

      Serial.println("Response code: " + httpCode);

      String payload = http.getString();

      Serial.println("Resultado: " + payload);
    }
  }
  void PostValueBySensor(int gxi,int gyi,int gzi,int axi,int ayi,int azi){
    if (WiFi.status() == WL_CONNECTED){
      HTTPClient http;
      http.begin(client, SERVER_IP, SERVER_PORT, "/api/mpu6050", true);
      http.addHeader("Content-Type", "application/json");

      const size_t capacity = JSON_OBJECT_SIZE(9) + JSON_ARRAY_SIZE(20) + 1000;
      DynamicJsonDocument doc(capacity);
      doc["idsensor_valor_mpu6050"] = 2;
      doc["idsensor"] = 1;
      doc["a_value_x"] = axi;
      doc["a_value_y"] = ayi;
      doc["a_value_z"] = azi;
      doc["g_value_x"] = gxi;
        doc["g_value_y"] = gyi;
          doc["g_value_z"] = gzi;
            doc["timestamp"] = 1235646879546;
      String output;
      serializeJson(doc, output);

      int httpCode = http.POST(output);

      Serial.println("Response code: " + httpCode);

      String payload = http.getString();

      Serial.println("Resultado: " + payload);
    }
  }

//Usuario
  void sendGetUsuario(){
    if (WiFi.status() == WL_CONNECTED){
      HTTPClient http;
      http.begin(client, SERVER_IP, SERVER_PORT, "/api/usuario/3", true);
      int httpCode = http.GET();

      Serial.println("Response code: " + httpCode);

      String payload = http.getString();

      const size_t capacity = JSON_OBJECT_SIZE(9) + JSON_ARRAY_SIZE(10) + 60;
      DynamicJsonDocument doc(capacity);

      DeserializationError error = deserializeJson(doc, payload);
      if (error){
        Serial.print("deserializeJson() failed: ");
        Serial.println(error.c_str());
        return;
      }
      Serial.println(F("Response:"));

      int idusuario=doc["idusuario"].as<int>();
      String nombre=doc["nombre"].as<String>();
      long tlf_usuario=doc["tlf_usuario"].as<long>();
      String correo_usuario=doc["correo_usuario"].as<String>();
      long tlf_emergencia=doc["tlf_emergencia"].as<long>();
      String modelo_moto=doc["modelo_moto"].as<String>();


        Serial.println("idusuario: " + String(idusuario));
        Serial.println("nombre: " + nombre);
        Serial.println("tlf_usuario: " + String(tlf_usuario));
        Serial.println("correo_usuario: " + correo_usuario);
        Serial.println("tlf_emergencia: " + String(tlf_emergencia));
        Serial.println("modelo_moto: " + modelo_moto);

        Serial.setTimeout(10000);
    }
  }
  void putUsuario(){
    if (WiFi.status() == WL_CONNECTED){
      HTTPClient http;
      http.begin(client, SERVER_IP, SERVER_PORT, "/api/usuario", true);
      http.addHeader("Content-Type", "application/json");

      const size_t capacity = JSON_OBJECT_SIZE(10) + JSON_ARRAY_SIZE(2) + 60;
      DynamicJsonDocument doc(capacity);
    //  doc["idusuario"] = 4;
      doc["nombre"] = "Oscar";
      doc["tlf_usuario"] = 666524312;
      doc["correo_usuario"] = "pruebaslocas@gmail.com";
      doc["tlf_emergencia"] = "112";
      doc["modelo_moto"] = "BMW K1";

      String output;
      serializeJson(doc, output);

      int httpCode = http.PUT(output);

      Serial.println("Response code: " + httpCode);

      String payload = http.getString();

      Serial.println("Resultado: " + payload);
    }
  }
  void postUsuarioActualiza(){
    if (WiFi.status() == WL_CONNECTED){
      HTTPClient http;
      http.begin(client, SERVER_IP, SERVER_PORT, "/api/usuario", true);
      http.addHeader("Content-Type", "application/json");

      const size_t capacity = JSON_OBJECT_SIZE(10) + JSON_ARRAY_SIZE(2) + 60;
      DynamicJsonDocument doc(capacity);
      doc["idusuario"] = 3;
      doc["nombre"] = "Oscar";
      doc["tlf_usuario"] = 666524312;
      doc["correo_usuario"] = "pruebaslocas@gmail.com";
      doc["tlf_emergencia"] = "911";
      doc["modelo_moto"] = "BMW KANEDA";
      String output;
      serializeJson(doc, output);

      int httpCode = http.POST(output);

      Serial.println("Response code: " + httpCode);

      String payload = http.getString();

      Serial.println("Resultado: " + payload);
    }
  }


//Dispositivo
void getDispositivo(){
  if (WiFi.status() == WL_CONNECTED){
    HTTPClient http;
    http.begin(client, SERVER_IP, SERVER_PORT, "/api/dispositivo/", true);//"/api/usuario/:idusuario"
    int httpCode = http.GET();

    Serial.println("Response code: " + httpCode);

    String payload = http.getString();

    const size_t capacity = JSON_OBJECT_SIZE(9) + JSON_ARRAY_SIZE(10) + 60;
    DynamicJsonDocument doc(capacity);

    DeserializationError error = deserializeJson(doc, payload);
    if (error){
      Serial.print("deserializeJson() failed: ");
      Serial.println(error.c_str());
      return;
    }
    Serial.println(F("Response:"));

    int iddispositivo=doc["iddispositivo"].as<int>();
    String ip=doc["ip"].as<String>();
    String nombre=doc["nombre"].as<String>();
    int idusuario=doc["idusuario"].as<int>();
    long initialtimestamp=doc["initialtimestamp"].as<long>();


      Serial.println("iddispositivo: " + String(iddispositivo));
      Serial.println("ip: " + ip);
      Serial.println("nombre: " + nombre);
      Serial.println("idusuario: " +String( idusuario));
      Serial.println("initialtimestamp: " + String(initialtimestamp));


      Serial.setTimeout(10000);
  }
}
//gps
void putGps(String coord){
  if (WiFi.status() == WL_CONNECTED){
    HTTPClient http;
    http.begin(client, SERVER_IP, SERVER_PORT, "/api/sensor/values/gps", true);
    http.addHeader("Content-Type", "application/json");

    const size_t capacity = JSON_OBJECT_SIZE(3) + JSON_ARRAY_SIZE(5) + 600;
    DynamicJsonDocument doc(capacity);
    doc["idsensor_valor_mpu6050"] = 4;
    doc["idsensor"] = 2;
    doc["value"] = coord;



    String output;
    serializeJson(doc, output);

    int httpCode = http.PUT(output);

    Serial.println("Response code: " + httpCode);

    String payload = http.getString();

    Serial.println("Resultado: " + payload);
  }
}
void postGps(String coord){
  if (WiFi.status() == WL_CONNECTED){
    HTTPClient http;
    http.begin(client, SERVER_IP, SERVER_PORT, "/api/sensor/values/gps", true);
    http.addHeader("Content-Type", "application/json");

    const size_t capacity = JSON_OBJECT_SIZE(3) + JSON_ARRAY_SIZE(2) + 600;
    DynamicJsonDocument doc(capacity);
    doc["idsensor_valor_mpu6050"] = 2;
    doc["idsensor"] = 2;
    doc["localizacion"] = coord;


    String output;
    serializeJson(doc, output);

    int httpCode = http.POST(output);

    Serial.println("Response code: " + httpCode);

    String payload = http.getString();

    Serial.println("Resultado: " + payload);
  }
}

  void sendPostRequest(){
    if (WiFi.status() == WL_CONNECTED){
      HTTPClient http;
      http.begin(client, SERVER_IP, SERVER_PORT, "/api/usuario", true);//"/api/usuario", https://run.mocky.io/v3/7e72bb8b-4a7d-4089-9d66-053dea732c03
      http.addHeader("Content-Type", "application/json");

      const size_t capacity = JSON_OBJECT_SIZE(3) + JSON_ARRAY_SIZE(2) + 60;
      DynamicJsonDocument doc(capacity);
      doc["temperature"] = 18;
      doc["humidity"] = 78;
      doc["timestamp"] = 124123123;
      doc["name"] = "sensor1";

      String output;
      serializeJson(doc, output);

      int httpCode = http.PUT(output);

      Serial.println("Response code: " + httpCode);

      String payload = http.getString();

      Serial.println("Resultado: " + payload);
    }
  }

  void getGps(){
    if (WiFi.status() == WL_CONNECTED){
      HTTPClient http;
      http.begin(client, SERVER_IP, SERVER_PORT, "/api/sensor/values/gps/2", true);
      int httpCode = http.GET();

      Serial.println("Response code: " + httpCode);

      String payload = http.getString();

      const size_t capacity = JSON_OBJECT_SIZE(4) + JSON_ARRAY_SIZE(12) + 1000;
      DynamicJsonDocument doc(capacity);

      DeserializationError error = deserializeJson(doc, payload);
      if (error){
        Serial.print("deserializeJson() failed: ");
        Serial.println(error.c_str());
        return;
      }
      Serial.println(F("Response:"));

      int idsensor_valor_mpu6050=doc[0][("idsensor_valor_mpu6050")].as<int>();
      int idsensor=doc[0][("idsensor")].as<int>();
      String localizacion=doc[0]["localizacion"].as<char*>();



        Serial.println("idsensor_valor_mpu6050: " + String(idsensor_valor_mpu6050));
        Serial.println("idsensor: " + String(idsensor));
        Serial.println("localizacion: " + localizacion);



    }
  }
  void putDispositivo(){
    if (WiFi.status() == WL_CONNECTED){
      HTTPClient http;
      http.begin(client, SERVER_IP, SERVER_PORT, "/api/dispositivo", true);//"/api/usuario", https://run.mocky.io/v3/7e72bb8b-4a7d-4089-9d66-053dea732c03
      http.addHeader("Content-Type", "application/json");

      const size_t capacity = JSON_OBJECT_SIZE(3) + JSON_ARRAY_SIZE(2) + 60;
      DynamicJsonDocument doc(capacity);
      doc["iddispositivo"] = 4;
      doc["ip"] = "192.168.1.108";
      doc["nombre"] = "yamaha";
      doc["idusuario"] = 1;
      doc["initialtimestamp"] = 135498431561;


      String output;
      serializeJson(doc, output);

      int httpCode = http.PUT(output);

      Serial.println("Response code: " + httpCode);

      String payload = http.getString();

      Serial.println("Resultado: " + payload);
    }
  }
  void postDispositivaActualiza(){
    if (WiFi.status() == WL_CONNECTED){
      HTTPClient http;
      http.begin(client, SERVER_IP, SERVER_PORT, "/api/dispositivo", true);
      http.addHeader("Content-Type", "application/json");

      const size_t capacity = JSON_OBJECT_SIZE(3) + JSON_ARRAY_SIZE(2) + 60;
      DynamicJsonDocument doc(capacity);
      doc["iddispositivo"] = 4;
      doc["ip"] = "192.168.1.104";
      doc["nombre"] = "BMW";
      doc["idusuario"] = 1;
      doc["initialtimestamp"] = 135498431561;


      String output;
      serializeJson(doc, output);

      int httpCode = http.POST(output);

      Serial.println("Response code: " + httpCode);

      String payload = http.getString();

      Serial.println("Resultado: " + payload);
    }
  }
