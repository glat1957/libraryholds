// Giorgio Latour
// Library Hold Servlet
// IHRTLUHC
package libraryhold;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import libraryhold.LibraryDAO;

public class HoldTitle extends HttpServlet {

    LibraryDAO model = new LibraryDAO();

    @Override
    public void init() throws ServletException {
        model.initDB();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        HttpSession session = request.getSession();

        String patron = request.getParameter("cardnumber");
        String selectedItem;
        
        // Determine if item was selected from select element for multiple results
        // or if the search returned just one result and set the title as selected.
        if(request.getParameter("itemlist") != null)
            selectedItem = request.getParameter("itemlist");
        else
            selectedItem = session.getAttribute("itemid").toString();
        
        int patronNum = model.getPatronNum(patron);
        if (patronNum == 0) {
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Holding Error</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1 style = \"text-align: center; font-family: helvetica\">Holding Error: Patron Not Found</h1>");
            out.println("<hr>");
            out.println("<hr style=\"height: 25pt; visibility: hidden;\"/>");
            out.println("<p style = \"font-family: helvetica; text-align: center;\">");
            out.println("Library patron not found. Click the link below to start again.");
            out.println("<br>");
            out.println("<br>");
            out.println("<a href = \"http://localhost:8080/LibraryHoldLatour/\">Return Home</a>");
            out.println("</p>");
            out.println("</body>");
            out.println("</html>");
        } else {
            try {
                boolean held = model.checkIfItemIsAlreadyHeld(selectedItem, patron);
                if (held) {
                    throw new Exception("Item already held.");
                }
                model.HoldItem(selectedItem, patronNum);
                out.println("<html>");
                out.println("<head>");
                out.println("<title>Hold Successful</title>");
                out.println("</head>");
                out.println("<body>");
                out.println("<h1 style = \"text-align: center; font-family: helvetica\">Hold Successful</h1>");
                out.println("<hr>");
                out.println("<hr style=\"height: 25pt; visibility: hidden;\"/>");
                out.println("<p style = \"font-family: helvetica; text-align: center;\">"
                        + "The title was successfully placed on hold. There are currently "
                        + model.getNumberOfHolds(selectedItem) + " holds for this item.</p>");
                out.println("<br>");
                out.println("<p style = \"font-family: helvetica; text-align: center;\">"
                        + "Click the link below to start again.");
                out.println("<br>");
                out.println("<br>");
                out.println("<a href = \"http://localhost:8080/LibraryHoldLatour/\">Return Home</a>");
                out.println("</p>");
                out.println("</body>");
                out.println("</html>");
            } catch (Exception ex) {
                ex.printStackTrace();
                out.println("<html>");
                out.println("<head>");
                out.println("<title>Holding Error</title>");
                out.println("</head>");
                out.println("<body>");
                out.println("<h1 style = \"text-align: center; font-family: helvetica\">Holding Error: Unknown Error</h1>");
                out.println("<hr>");
                out.println("<hr style=\"height: 25pt; visibility: hidden;\"/>");
                out.println("<p style = \"font-family: helvetica; text-align: center;\">");
                out.println("An unknown error occurred. It may be that you already put this book on hold. Click the link below to start again.");
                out.println("<br>");
                out.println("<br>");
                out.println("<a href = \"http://localhost:8080/LibraryHoldLatour/\">Return Home</a>");
                out.println("</p>");
                out.println("</body>");
                out.println("</html>");
            }
        }
    }

    @Override
    public void destroy() {
        try {
            model.getConnection().close();
        } catch (SQLException ex) {
        }
    }
}
