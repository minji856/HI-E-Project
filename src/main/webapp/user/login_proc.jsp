<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="model.UserDao" %>
<html>
<head>
    <title>로그인 처리</title>
</head>
<body>
    <%
        // 인코딩 처리
        request.setCharacterEncoding("UTF-8"); 
        
        // 로그인 화면에 입력된 아이디와 비밀번호를 가져온다
        String empno= request.getParameter("empno");
        String pw = request.getParameter("password");
        
        // DB에서 아이디, 비밀번호 확인
        UserDao dao = UserDao.getInstance();
        int check = dao.loginCheck(empno, pw);
        session = request.getSession();
        
        
        // URL 및 로그인관련 전달 메시지
        String msg = "";
        
    	// 로그인 성공
        if(check == 1) { 	
            // 세션에 현재 아이디 세팅
            session.setAttribute("sessionID", empno);
            msg = "../index.jsp";
        }
     	// 비밀번호가 틀릴경우
        else if(check == 0) { 
            msg = "/ERP_Hi-E/user/login.jsp?msg=0";
        }
     	// 아이디가 틀릴경우
        else { 	
            msg = "/ERP_Hi-E/user/login.jsp?msg=-1";
        }
         
        // sendRedirect(String URL) : 해당 URL로 이동
        // URL뒤에 get방식 처럼 데이터를 전달가능
        response.sendRedirect(msg);
    %>
</body>
</html>
