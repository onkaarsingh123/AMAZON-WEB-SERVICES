package com.psl.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
 

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
 

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

 
@WebServlet("/user")
public class UserServlet extends HttpServlet 
{
  private static final long serialVersionUID = 1L;
 
  public UserServlet()
  {
    super();
  }
 
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
  {
    StringBuffer sb = new StringBuffer();
 
    try 
    {
      BufferedReader reader = request.getReader();
      String line = null;
      while ((line = reader.readLine()) != null)
      {
        sb.append(line);
      }
    } catch (Exception e) { e.printStackTrace(); }
 
    JSONParser parser = new JSONParser();
    JSONObject joUser = null;
    try
    {
      joUser = (JSONObject) parser.parse(sb.toString());
    } catch (ParseException e) { e.printStackTrace(); }
 
    String user = (String) joUser.get("name");
 
    response.setContentType("text/html");
    PrintWriter out = response.getWriter();
    out.write("User infomation.");
    out.write("A new user " + user + " has been created.");
    out.flush();
    out.close();
  }
}