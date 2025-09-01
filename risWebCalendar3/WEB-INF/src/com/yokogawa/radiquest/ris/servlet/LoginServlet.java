package com.yokogawa.radiquest.ris.servlet;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.yokogawa.radiquest.ris.action.LoginAction;

/**
 * ログインサーブレット(未使用)
 * 
 * @author Shogo TANIAI
 */
public class LoginServlet extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException {
		perform(request, response);
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException {
		perform(request, response);
	}

	private void perform(HttpServletRequest request,
			HttpServletResponse response) throws ServletException {
		LoginAction loginAction = new LoginAction();
		if (loginAction.permitLogin()) {
			forward(request, response);
		}
	}

	private void forward(HttpServletRequest request,
			HttpServletResponse response) throws ServletException {
		try {
			RequestDispatcher dispatcher = request
					.getRequestDispatcher("jsp/reservations.jsp");
			dispatcher.forward(request, response);
		} catch (IOException e) {
			throw new ServletException(e);
		}
	}
}
