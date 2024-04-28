package org.jgine.net.http.controller;

import maxLibs.net.http.HttpResponse;
import maxLibs.net.http.annotation.Method;
import maxLibs.net.http.annotation.Route;

public class HomeController extends HttpController {

	@Route("")
	@Route("index.html")
	@Route("Home")
	@Route("Home/Index")
	@Method("GET")
	public void index() {
		HttpResponse.sendResource(HttpResponse.HTML, "home/index.html");
	}

	@Route("HelloWorld")
	@Route("Home/HelloWorld")
	@Method("GET")
	public void helloWorld() {
		HttpResponse.sendString("Hello World");
	}
}
