// Giorgio Latour
// Library Hold Servlet
// IHRTLUHC
package libraryhold;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.ArrayList;

public class LibraryHoldServlet extends HttpServlet {
    
    ResultSet rset = null;
    ArrayList<String> items = new ArrayList<>();
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

        String keyword = request.getParameter("keyword");

        // Search database for keyword and get title(s) and item id(s).
        if (keyword != "") {
            try {
                items.clear();
                rset = model.searchItems(keyword);

                while (rset.next()) {
                    items.add(rset.getString(1));
                    items.add(rset.getString(2));
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } else {
            out.println("<html>");
            out.println("<head>");
            out.println("<title>No keyword entered.</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<br>");
            out.println("<h1 style = \"text-align: center; font-family: "
                    + "helvetica;\">You did not insert a keyword.</h1>");
            out.println(" <p style = \"text-align: center; font-family: helvetica;\">"
                    + "Please go back and enter a keyword to search for.</p>");
            out.println("</body>");
            out.println("</html>");
        }

        // No items found.
        if(items.isEmpty()){
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Results</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1 style = \"text-align: center; font-family: helvetica\">Results</h1>");
            out.println("<hr>");
            out.println("<hr style=\"height: 25pt; visibility: hidden;\"/>");
            out.println("<p style = \"font-family: helvetica; text-align: center;\">");
            out.println("No title was found. Click the link below to return to the home page.");
            out.println("<br>");
            out.println("<br>");
            out.println("<a href = \"http://localhost:8080/LibraryHoldLatour/\">Return Home</a>");
            out.println("</p>");
            out.println("</body>");
            out.println("</html>");
        }
        // One item consists of two entries in the array list, its id and title.
        // Display form to hold item if only one item is found.
        else if (items.size() == 2) {
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Results</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1 style = \"text-align: center; font-family: helvetica\">Results</h1>");
            out.println("<hr>");
            out.println("<br>");
            out.println("<table align = \"center\" style = \"font-family: helvetica\">");
            out.println("<tr>");
            out.println("<th>Item ID</th>");
            out.println("<th>Item Title</th>");
            out.println("</tr>");
            out.println("<tr>");
            out.println("<td>" + items.get(0) + "</td>");
            
            HttpSession session = request.getSession();
            session.setAttribute("itemid", items.get(0));
            
            out.println("<td>" + items.get(1) + "</td>");
            out.println("</tr>");
            out.println("</table>");
            out.println("<hr style=\"height: 15pt; visibility: hidden;\"/>");
            out.println("<div align=\"center\">");
            out.println("<p style = \"font-family: helvetica;\">"
                    + "To place this book on hold, please enter your library card number.</p>");
            out.println("<form action = \"HoldTitle\" method = \"post\" align = \"center\" id = \"holditem\">");
            out.println("<input type = \"text\" name = \"cardnumber\" style = \"font-size: 100%;\" required>");
            out.println("<input type = \"submit\" style = \"font-size: 100%;\" value = \"Hold\">");
            out.println("</form>");
            out.println("</div>");
            out.println("</body>");
            out.println("</html>");
        } else{
            //Display selection for multiple items and field for library card number.
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Results</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1 style = \"text-align: center; font-family: helvetica\">Results</h1>");
            out.println("<hr>");
            out.println("<hr style = \"height: 25pt; visibility: hidden;\"/>");
            out.println("<p style = \"font-family: helvetica; text-align: center;\">To "
                    + "place a book on hold, please select it and enter your library card number.</p>");
            out.println("<div align = \"center\">");
            out.println("<select name = \"itemlist\" form = \"holditem\" style = \"text-align: center\">");
            
            // Print HTML for all items in ResultSet items list.
            for(int i = 0; i < items.size(); i++){
                out.println("<option value = \"" + items.get(i) + "\">" + items.get(i) + " " + items.get(i+1) + "</option>");
                i++;
            }
            
            out.println("</select>");
            out.println("<br>");
            out.println("<br>");
            out.println("<form id = \"holditem\" action = \"HoldTitle\" method = \"post\">");
            out.println("<input type = \"text\" name = \"cardnumber\" style = \"font-size: 100%;\" required>");
            out.println("<input type = \"submit\" style = \"font-size: 100%;\" value = \"Hold\">");
            out.println("</form>");
            out.println("</div>");
            out.println("</body>");
            out.println("</html>");
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
