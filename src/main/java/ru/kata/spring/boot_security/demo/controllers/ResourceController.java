package ru.kata.spring.boot_security.demo.controllers;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

@Controller
public class ResourceController {

    @GetMapping("/assets/css/{code}.css")
    @ResponseBody
    public ResponseEntity<String> styles(@PathVariable("code") String code) throws IOException {
        StringBuffer sb = new StringBuffer();
        // получаем содержимое файла из папки ресурсов в виде потока
        try (
                InputStream is = getClass().getClassLoader().getResourceAsStream("static/assets/css/" + code + ".css");
                // преобразуем поток в строку
                BufferedReader bf = new BufferedReader(new InputStreamReader(is));
        ) {

            String line = null;
            while ((line = bf.readLine()) != null) {
                sb.append(line + "\n");
            }
        }


        // создаем объект, в котором будем хранить HTTP заголовки
        final HttpHeaders httpHeaders = new HttpHeaders();
        // добавляем заголовок, который хранит тип содержимого
        httpHeaders.add("Content-Type", "text/css; charset=utf-8");
        // возвращаем HTTP ответ, в который передаем тело ответа, заголовки и статус 200 Ok
        return new ResponseEntity<String>(sb.toString(), httpHeaders, HttpStatus.OK);
    }

    @GetMapping("/assets/js/{code}.js")
    @ResponseBody
    public ResponseEntity<String> js(@PathVariable("code") String code) throws IOException {
        StringBuffer sb = new StringBuffer();
        // получаем содержимое файла из папки ресурсов в виде потока
        try (
                InputStream is = getClass().getClassLoader().getResourceAsStream("static/assets/js/" + code + ".js");
                // преобразуем поток в строку
                BufferedReader bf = new BufferedReader(new InputStreamReader(is));
        ) {

            String line = null;
            while ((line = bf.readLine()) != null) {
                sb.append(line + "\n");
            }
        }


        // создаем объект, в котором будем хранить HTTP заголовки
        final HttpHeaders httpHeaders = new HttpHeaders();
        // добавляем заголовок, который хранит тип содержимого
        httpHeaders.add("Content-Type", "text/css; charset=utf-8");
        // возвращаем HTTP ответ, в который передаем тело ответа, заголовки и статус 200 Ok
        return new ResponseEntity<String>(sb.toString(), httpHeaders, HttpStatus.OK);
    }
}