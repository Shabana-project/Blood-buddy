package bloodbuddy.controller;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.*;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/RequestContactServlet")
public class RequestContactServlet extends HttpServlet {

    @SuppressWarnings("unchecked")
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String actionType = request.getParameter("actionType");

        if ("request".equals(actionType)) {
            String donorId = request.getParameter("donorId");
            String donorName = request.getParameter("donorName");
            String donorPhone = request.getParameter("donorPhone");
            String bloodGroup = request.getParameter("blood_group");
            String location = request.getParameter("location");
            String requestorName = request.getParameter("requestor_name");
            String requestorEmpId = request.getParameter("requestor_empid");
            String reason = request.getParameter("reason");

            ServletContext context = getServletContext();
            Map<String, Set<String>> contactLog = (Map<String, Set<String>>) context.getAttribute("contactLog");
            if (contactLog == null) {
                contactLog = new HashMap<>();
                context.setAttribute("contactLog", contactLog);
            }

            Set<String> contactedDonors = contactLog.getOrDefault(requestorEmpId, new HashSet<>());
            boolean alreadyRequested = contactedDonors.contains(donorId);

            if (alreadyRequested) {
                // Forward back to results.jsp with feedback
                List<bloodbuddy.controller.SearchDonorServlet.Donor> donors =
                        (List<bloodbuddy.controller.SearchDonorServlet.Donor>) context.getAttribute("lastSearchResults");
                Map<String, Boolean> alreadyRequestedMap = new HashMap<>();
                alreadyRequestedMap.put(donorId, true);

                request.setAttribute("donors", donors);
                request.setAttribute("alreadyRequestedMap", alreadyRequestedMap);
                RequestDispatcher dispatcher = request.getRequestDispatcher("results.jsp");
                dispatcher.forward(request, response);
                return;
            }

            // Track new contact
            contactedDonors.add(donorId);
            contactLog.put(requestorEmpId, contactedDonors);

            // Proceed to contact details
            request.setAttribute("donorId", donorId);
            request.setAttribute("donorName", donorName);
            request.setAttribute("donorPhone", donorPhone);
            request.setAttribute("bloodGroup", bloodGroup);
            request.setAttribute("location", location);
            request.setAttribute("requestorName", requestorName);
            request.setAttribute("requestorEmpId", requestorEmpId);
            request.setAttribute("reason", reason);

            RequestDispatcher dispatcher = request.getRequestDispatcher("donorcontactdetails.jsp");
            dispatcher.forward(request, response);

        } else if ("sendSMS".equals(actionType)) {
            String donorName = request.getParameter("donorName");
            String donorPhone = request.getParameter("donorPhone");

            try {
                String apiKey = "VCw8odQev3MF2TCKrOFETAB9ewtqP4OL6aVGH98I0PGfCcLxBn9GxcoXHM6F";
                String message = "Hi " + donorName + ",\nA BloodBuddy user is trying to reach you.\nPlease expect a call soon.";
                String encodedMessage = URLEncoder.encode(message, "UTF-8");

                String url = "https://www.fast2sms.com/dev/bulkV2?" +
                             "authorization=" + apiKey +
                             "&sender_id=FSTSMS" +
                             "&message=" + encodedMessage +
                             "&language=english" +
                             "&route=q" +
                             "&numbers=" + donorPhone;

                HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("cache-control", "no-cache");
                conn.setRequestProperty("User-Agent", "Mozilla/5.0");

                int code = conn.getResponseCode();
                System.out.println("✅ SMS sent! Response code: " + code);

                response.sendRedirect("donorcontactdetails.jsp?sms=true&donorName=" + URLEncoder.encode(donorName, "UTF-8"));
            } catch (Exception e) {
                System.out.println("❌ Error sending SMS: " + e.getMessage());
                response.setContentType("text/html");
                response.getWriter().println(
                    "<script>alert('❌ Failed to send SMS. Please try again.'); window.location='donorcontactdetails.jsp';</script>"
                );
            }
        } else {
            response.getWriter().println("⚠️ Unknown action. Please check your form submission.");
        }
    }
}
