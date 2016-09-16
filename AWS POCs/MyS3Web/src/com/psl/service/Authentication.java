package com.psl.service;

import java.io.IOException;
import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class Authentication extends HttpServlet  {

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		DirContext ctx;
		String path=request.getContextPath();
		try {
			HttpSession session=request.getSession();
			String email = request.getParameter("email");
			String password = request.getParameter("password");
			System.out.println("email="+email);
			System.out.println("password="+password);
			String array[];
			array = email.split("@");
			String username = array[0];
			String role ="admin";
			
			Hashtable<String, String> env = new Hashtable<String, String>(11);
			env.put(Context.INITIAL_CONTEXT_FACTORY,
					"com.sun.jndi.ldap.LdapCtxFactory");
			env.put(Context.SECURITY_AUTHENTICATION, "simple");
			env.put(Context.SECURITY_PRINCIPAL, email);
			env.put(Context.SECURITY_CREDENTIALS, password);
			env.put(Context.PROVIDER_URL, "ldap://marines.local:389");

			// Authenticate the logon user
			ctx = new InitialDirContext(env);
			// Start checking if the user is within the organization unit(s)
			String searchBase = "ou=marineCrop,DC=marines,DC=local";
			StringBuilder searchFilter = new StringBuilder("(&");
			searchFilter.append("(objectClass=person)");
			searchFilter.append("(userPrincipalName=" + email + ")");
			searchFilter
					.append("(memberOf=CN=marineAdmin,OU=marineCrop,DC=marines,DC=local)");
			searchFilter.append(")");

			// String searchFilter =
			// "(&(objectClass=person)(sAMAccountName=jamesS)(userPrincipalName=jamesS@marines.local))";
			SearchControls sCtrl = new SearchControls();
			sCtrl.setSearchScope(SearchControls.SUBTREE_SCOPE);
			NamingEnumeration answer = ctx.search(searchBase,
					searchFilter.toString(), sCtrl);
			boolean pass = false;
			System.out.println("REssult ");
			while (answer.hasMoreElements()) {
				SearchResult sr = (SearchResult) answer.next();
				System.out.println("REssult " + sr);
				 String str=(String)sr.getAttributes().get("memberOf").get(1);
			         String arr[]=str.split(",");
					for(int i=0;i<arr.length;i++){
						System.out.println(arr[i]);
						if(arr[i].equals("CN=Domain Admins")){
							role="admin";
						}else{
							role="engineer";
						}
					}
				pass = true;
			}
			if (pass) {
				System.out.println("User Authenticated successfully!!");
				session.setAttribute("email", email);
				session.setAttribute("userRole",role);
				response.sendRedirect(path+"/pages/createS3Bucket.jsp");
				// The user belongs to the specified OU(s), do something...
			} else {
				// The user doesn't belong to the specified OU(s)
				System.out.println("Authentication failed : ");
				response.sendRedirect(path+"/pages/Error.jsp");
			}

		} catch (Exception e) {
			System.out.println("Authentication failed : " + e);
			e.printStackTrace();
			response.sendRedirect(path+"/pages/Error.jsp");
			
		}

	}

}
