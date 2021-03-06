package fr.fred.servlet;

import fr.fred.beans.User;
import fr.fred.modele.UsersServices;
import fr.fred.util.AnalyzeUriResource;
import fr.fred.util.WebUtil;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Frederic on 28/12/13.
 */
public class Users extends HttpServlet {

   final private static Logger logger = LoggerFactory.getLogger(Users.class);

   @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
       final AnalyzeUriResource resource = WebUtil.analyzeUriResource(req);
       logger.info("doGet method. Resource : {}; id : {}", resource.getResource(), resource.getId());
       showListForm(req, resp, resource);

       showDetailOrUpdateForm(req, resp, resource);

       showCreateForm(req, resp, resource);

    }

   @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
       final AnalyzeUriResource resource = WebUtil.analyzeUriResource(req);
      logger.info("doPut method. Resource : {}; id : {}", resource.getResource(), resource.getId());
        updateUser(req, resp, resource);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
       final AnalyzeUriResource resource = WebUtil.analyzeUriResource(req);
       logger.info("doDelete method. Resource : {}; id : {}", resource.getResource(), resource.getId());
       deleteUser(req, resp, resource);
    }

   @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
   {
      final AnalyzeUriResource resource = WebUtil.analyzeUriResource(req);
      logger.info("doPost method. Resource : {}; id : {}", resource.getResource(), resource.getId());
      createUser(req, resp, resource);

   }

   /**
    * Mise à jour de l'utilisateur
    * @param req HTTP Request
    * @param resp HTTP Response
    * @param resource décomposition REST
    * @throws ServletException en cas d'erreur
    */
   private void updateUser(HttpServletRequest req, HttpServletResponse resp, AnalyzeUriResource resource) throws ServletException, IOException {
      if("user".equals((resource.getResource())))
      {
         final User user = initializeUserFromView(req, resource.getId());
         UsersServices.getService().updateUser(user);
         resp.sendRedirect(req.getContextPath()+"/users/");
      }
   }

   private void showListForm(HttpServletRequest req, HttpServletResponse resp, AnalyzeUriResource resource) throws ServletException, IOException
   {
      if ("".equals(resource.getResource())) {
         final Collection<User> users = UsersServices.getService().getListOfUsers();
         req.setAttribute("users", users);
         getServletContext().getRequestDispatcher("/listOfUsers.jsp").forward(req, resp);
      }
   }

   private void deleteUser(HttpServletRequest req, HttpServletResponse resp, AnalyzeUriResource resource) throws IOException
   {
      if("user".equals((resource.getResource())))
      {
         UsersServices.getService().delete(resource.getId());
         resp.sendRedirect(req.getContextPath()+"/users/");
      }
   }

   private void showDetailOrUpdateForm(HttpServletRequest req, HttpServletResponse resp, AnalyzeUriResource resource) throws ServletException, IOException
   {
      if ("user".equals(resource.getResource())) {
         final User user = UsersServices.getService().getUser(resource.getId());
         req.setAttribute("user", user);


         if(req.getParameter("update") != null)
         {
            req.setAttribute("disabled", "");
            req.setAttribute("method", "post");
            req.setAttribute("action", "");
            req.setAttribute("operation", "update");
         }
         else{
            req.setAttribute("disabled", "disabled");
            req.setAttribute("method", "get");
         }

         getServletContext().getRequestDispatcher("/user.jsp").forward(req, resp);
      }
   }

   private void showCreateForm(HttpServletRequest req, HttpServletResponse resp, AnalyzeUriResource resource) throws ServletException, IOException
   {
      if ("new".equals(resource.getResource())) {
         final User user = new User();
         req.setAttribute("user", user);
         req.setAttribute("disabled", "");
         req.setAttribute("method","post");
         req.setAttribute("action", "user/new");
         req.setAttribute("operation", "create");
         getServletContext().getRequestDispatcher("/user.jsp").forward(req, resp);
      }
   }

   private void createUser(HttpServletRequest req, HttpServletResponse resp, AnalyzeUriResource resource) throws ServletException, IOException
   {
      if ("new".equals(resource.getResource())) {
          final User user = initializeUserFromView(req, null);
          UsersServices.getService().createUser(user);
          resp.sendRedirect(req.getContextPath()+"/users/");
      }
   }

   /**
     * Initialise les champs utilisateurs avec ceux saisis sur la vue
     * @param req requête HTTP
     * @return une instance de User dont les champs sont initialisés
     * @throws ServletException en cas d'erreur
     */
    private User initializeUserFromView(HttpServletRequest req, Long id) throws ServletException {
        final User user = id == null ? new User() : UsersServices.getService().getUser(id);
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
        return user;
    }


}
