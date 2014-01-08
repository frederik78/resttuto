package fr.fred.servlet;

import fr.fred.beans.User;
import fr.fred.modele.UsersServices;
import fr.fred.util.AnalyzeUriResource;
import fr.fred.util.WebUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

/**
 * Created by Frederic on 28/12/13.
 */
public class Users extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        final AnalyzeUriResource resource = WebUtil.analyzeUriResource(req);
        if ("".equals(resource.getResource())) {
            final Collection<User> users = UsersServices.getService().getListOfUsers();
            req.setAttribute("users", users);
            getServletContext().getRequestDispatcher("/listOfUsers.jsp").forward(req, resp);
        }

        if ("user".equals(resource.getResource())) {
            final User user = UsersServices.getService().getUser(resource.getId());
            req.setAttribute("user", user);
            req.setAttribute("disabled", "disabled");
            req.setAttribute("method","get");
            getServletContext().getRequestDispatcher("/user.jsp").forward(req, resp);
        }

        if ("new".equals(resource.getResource())) {
            final User user = new User();
            req.setAttribute("user", user);
            req.setAttribute("disabled", "");
            req.setAttribute("method","post");
            getServletContext().getRequestDispatcher("/user.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPut(req, resp);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doDelete(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        final AnalyzeUriResource resource = WebUtil.analyzeUriResource(req);
        if ("new".equals(resource.getResource())) {
            final User user = new User();
            user.setFirstname(req.getParameter("firstname"));
            user.setLastname(req.getParameter("lastname"));
            user.setEmail(req.getParameter("email"));
            user.setPhone(req.getParameter("phone"));

            final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
            final Date birthDay;
            try {
                birthDay = simpleDateFormat.parse(req.getParameter("birthday"));
            } catch (ParseException e) {
                throw new ServletException("Mauvaise saisie de date");
            }
            user.setBirthday(birthDay);
            UsersServices.getService().createUser(user);
            resp.sendRedirect("/users/");
        }

    }


}
