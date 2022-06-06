package org.jgine.net.http.controller;

import org.jgine.net.http.HttpResponse;
import org.jgine.net.http.annotation.Method;
import org.jgine.net.http.annotation.Route;

public class HomeController extends HttpController {

	@Route("")
	@Route("index.html")
	@Route("Home")
	@Route("Home/Index")
	@Method("GET")
	public void index() {
		HttpResponse.sendPage("home/index.html");
	}

	@Route("HelloWorld")
	@Route("Home/HelloWorld")
	@Method("GET")
	public void helloWorld() {
		HttpResponse.sendString("Hello World");
	}
}
