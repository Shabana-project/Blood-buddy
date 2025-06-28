<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.*, bloodbuddy.controller.SearchDonorServlet.Donor" %>

<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <title>Search Results | BloodBuddy</title>
  <style>
    body {
      font-family: Arial, sans-serif;
      background: linear-gradient(to bottom, #fff3f3 0%, #ffffff 100%);
      margin: 0;
      padding: 40px;
    }

    .results-container {
      background: #fff;
      padding: 40px;
      max-width: 1000px;
      margin: auto;
      border-radius: 12px;
      box-shadow: 0 6px 20px rgba(0, 0, 0, 0.08);
    }

    h2 {
      color: #c62828;
      text-align: center;
      margin-bottom: 0;
    }

    .subheading {
      text-align: center;
      color: #777;
      font-size: 16px;
      margin-top: 6px;
      margin-bottom: 30px;
    }

    table {
      width: 100%;
      border-collapse: collapse;
      margin-top: 10px;
    }

    th, td {
      padding: 12px;
      text-align: left;
      border-bottom: 1px solid #ddd;
    }

    th {
      background-color: #cc0000;
      color: white;
    }

    tr:hover {
      background-color: #f9f9f9;
    }

    button {
      background-color: #c62828;
      color: white;
      border: none;
      padding: 8px 14px;
      font-size: 14px;
      border-radius: 6px;
      cursor: pointer;
    }

    button:hover {
      background-color: #a91d1d;
    }

    .request-form {
      display: none;
      margin-top: 10px;
      padding: 10px 0;
    }

    .request-form input,
    .request-form textarea {
      width: 100%;
      padding: 8px;
      margin-bottom: 10px;
      font-size: 14px;
    }

    .request-form button {
      background-color: #c62828;
    }

    .nav-link {
      margin-top: 40px;
      text-align: center;
      font-size: 14px;
    }

    .nav-link a {
      text-decoration: none;
      color: #c62828;
      font-weight: bold;
      margin: 0 10px;
    }

    .nav-link a:hover {
      text-decoration: underline;
    }
  </style>

  <script>
    function toggleForm(empId) {
      const form = document.getElementById("form-" + empId);
      form.style.display = form.style.display === "none" ? "block" : "none";
    }
  </script>
</head>
<body>

  <div class="results-container">
    <h2>Matching Donors</h2>
    <p class="subheading">Here are your life-saving matches. Contact responsibly.</p>

    <%
      List<Donor> donors = (List<Donor>) request.getAttribute("donors");
      if (donors == null || donors.isEmpty()) {
    %>
      <p>No donors found for the selected blood group and location.</p>
    <%
      } else {
    %>
    <table>
      <tr>
        <th>Name</th>
        <th>Blood Group</th>
        <th>Location</th>
        <th>Employee ID</th>
        <th>Action</th>
      </tr>
      <%
        for (Donor d : donors) {
          String empId = d.getEmployeeId();
      %>
      <tr>
        <td><%= d.getName() %></td>
        <td><%= d.getBloodGroup() %></td>
        <td><%= d.getLocation() %></td>
        <td><%= empId %></td>
        <td>
          <button onclick="toggleForm('<%= empId %>')">📩 Request Contact</button>
          <div class="request-form" id="form-<%= empId %>">
            <form action="#" method="post">
              <input type="hidden" name="donor_empid" value="<%= empId %>">
              <label>Your Name:</label>
              <input type="text" name="requester_name" required>
              <label>Your Employee ID:</label>
              <input type="text" name="requester_empid" required>
              <label>Reason:</label>
              <textarea name="reason" rows="2"></textarea>
              <button type="submit">Send Request</button>
            </form>
          </div>
        </td>
      </tr>
      <%
        }
      %>
    </table>
    <%
      }
    %>

    <div class="nav-link">
      <a href="index.html">🏠 Home</a> |
      <a href="search.html">🔍 Search Again</a> |
      <a href="register.html">🩸 Register as Donor</a>
    </div>
  </div>

</body>
</html>
