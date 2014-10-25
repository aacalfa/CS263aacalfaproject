
package cs263aacalfaproject.cs263aacalfaproject;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ImageDisplayServlet extends HttpServlet {
  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws IOException {

//    UserService userService = UserServiceFactory.getUserService();
//    User currentUser = userService.getCurrentUser();
//
//    if (currentUser != null) {
//    	imgByt = imageClass.getPhotograph();//return blob...
//    	resp.setContentType("image/jpg");
//    	resp.getOutputStream().write(imgByt);
//    	resp.getOutputStream().flush();
//    	resp.getOutputStream().close();
//    } else {
//      resp.sendRedirect(userService.createLoginURL(req.getRequestURI()));
//    }
  }
}

