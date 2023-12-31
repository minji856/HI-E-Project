package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.naming.NamingException;

import dbcp.DBConnection;


/* Data Access Object
 * 테이블 당 한개의 DAO를 작성한다.
 * 
 * EMP_USER 테이블과 연관된 DAO로
 * 회원 데이터를 처리하는 클래스이다.
 */
public class UserDao {
	private static UserDao instance;
	
	// 싱글톤 패턴
	private UserDao(){}
	public static UserDao getInstance(){
		if(instance==null)
			instance=new UserDao();
		return instance;
	}
	
	// 회원정보를 EMP_USER 테이블에 저장하는 메서드
	public void insertUser(UserBean user) throws Exception{
		DBConnection dbc = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		
		try {
			// 커넥션을 가져온다.
			dbc = DBConnection.getInstance();
			conn = dbc.getConnection();
			
			// 자동 커밋을 false로 한다.
			conn.setAutoCommit(false);
			
			// 쿼리 생성한다.
			// 가입일의 경우 자동으로 세팅되게 하기 위해 sysdate를 사용
			StringBuffer sql = new StringBuffer();
			sql.append("insert into EMP_USER values ");
			sql.append(" (?, ?, ?, ?, ?, ?, ?,sysdate) ");		
			/* 
			 * StringBuffer에 담긴 값을 얻으려면 toString()메서드를
			 * 이용해야 한다.
			 */
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setString(1, user.getEmpno());
			pstmt.setString(2, user.getPassword());
			pstmt.setString(3, user.getName());
			pstmt.setInt(4, user.getBirthday());
			pstmt.setString(5, user.getEmail());
			pstmt.setString(6, user.getAddress());
			pstmt.setString(7, user.getPhonenumber());
			
			// 쿼리 실행
			pstmt.executeUpdate();
			// 완료시 커밋
			conn.commit(); 
			
		} catch (ClassNotFoundException | NamingException | SQLException sqle) {
			// 오류시 롤백
			conn.rollback(); 
			
			throw new RuntimeException(sqle.getMessage());
		} finally {
			dbc.freeConnection(conn,pstmt);
		}  
	}
	
	// 로그인시 아이디, 비밀번호 체크 메서드
    // 아이디, 비밀번호를 인자로 받는다.
    public int loginCheck(String empno, String pw) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        DBConnection dbc = null;
 
        String dbPW = ""; // db에서 꺼낸 비밀번호를 담을 변수
        int x = -1;
 
        try {
            // 쿼리 - 먼저 입력된 아이디로 DB에서 비밀번호를 조회한다.
            StringBuffer query = new StringBuffer();
            query.append("SELECT PASSWORD FROM EMP_USER WHERE EMPNO = ? ");
            dbc = DBConnection.getInstance();
			conn = dbc.getConnection();
            pstmt = conn.prepareStatement(query.toString());
            pstmt.setString(1, empno);
            rs = pstmt.executeQuery();
 
            if (rs.next()) {	// 입력된 아이디에 해당하는 비번 있을경우
                dbPW = rs.getString("password"); // 비번을 변수에 넣는다.
 
                if (dbPW.equals(pw)) 
                    x = 1; // 넘겨받은 비번과 꺼내온 비번 비교. 같으면 인증성공 => 1리턴
                else                  
                    x = 0; // DB의 비밀번호와 입력받은 비밀번호 다름, 인증실패 => 0리턴
                
            } else {
                x = -1; // 해당 아이디가 없을 경우 => -1 리턴
            }
            return x;
        } catch (Exception sqle) {
            throw new RuntimeException(sqle.getMessage());
        } finally {
			dbc.freeConnection(conn,pstmt,rs);
        }
    }
}
