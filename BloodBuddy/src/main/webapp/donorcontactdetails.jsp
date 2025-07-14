<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="javax.servlet.http.*, javax.servlet.*" %>
<html>
<head>
  <title>Donor Contact Details</title>
  <style>
    body {
      font-family: sans-serif;
      background-color: #fff7f7;
      padding: 40px 20px;
    }

    .container {
      background-color: #fff;
      border: 1px solid #ddd;
      padding: 24px;
      max-width: 500px;
      margin: auto;
      box-shadow: 0 2px 5px rgba(0,0,0,0.1);
      position: relative;
    }

    h2 {
      color: #c62828;
      text-align: center;
      margin-bottom: 24px;
    }

    p {
      margin: 8px 0;
    }

    label {
      font-weight: bold;
    }

    button {
      background-color: #c62828;
      color: white;
      padding: 10px;
      border: none;
      cursor: pointer;
      margin-top: 20px;
      width: 100%;
      font-weight: bold;
    }

    button:disabled {
      background-color: #ccc;
      cursor: not-allowed;
    }

    .nav-link {
      margin-top: 30px;
      text-align: center;
    }

    .toast {
      color: green;
      font-weight: bold;
      text-align: center;
      margin-top: 20px;
      animation: fadein 0.5s;
    }

    .cooldown {
      color: #888;
      font-style: italic;
      text-align: center;
      margin-top: 12px;
    }

    @keyframes fadein {
      from {opacity: 0;}
      to {opacity: 1;}
    }
  </style>
</head>
<body>

  <div class="container">
    <h2>Donor Contact Details</h2>

    <p><label>Name:</label> ${donorName}</p>
    <p><label>Blood Group:</label> ${bloodGroup}</p>
    <p><label>Location:</label> ${location}</p>
    <p><label>Employee ID:</label> ${donorId}</p>
    <p><label>Phone Number:</label> ${donorPhone}</p>

    <form id="smsForm">
      <input type="hidden" name="actionType" value="sendSMS">
      <input type="hidden" name="donorPhone" value="${donorPhone}">
      <input type="hidden" name="donorName" value="${donorName}">
      <button type="submit">📨 Send SMS Notification</button>
    </form>

    <div class="nav-link">
      <a href="search.html">🔍 Back to Search</a>
    </div>
  </div>

  <script>
    document.getElementById("smsForm").addEventListener("submit", function(e) {
      e.preventDefault();

      const form = e.target;
      const button = form.querySelector("button");
      const formData = new FormData(form);

      // Disable button and update appearance
      button.disabled = true;
      button.textContent = "SMS Sent ✅";

      // Show toast notification
      showToast("✅ SMS sent successfully. Contact responsibly!");

      // Show cooldown message
      showCooldownMessage("⏳ You may contact the donor again after 30 minutes. Please come back later.");

      // Send SMS request
      fetch("RequestContactServlet", {
        method: "POST",
        body: formData
      })
      .then(response => response.text())
      .catch(error => {
        alert("❌ Failed to send SMS. Please try again.");
        console.error("Error:", error);

        // Re-enable button and remove message if sending failed
        button.disabled = false;
        button.textContent = "📨 Send SMS Notification";
        removeCooldownMessage();
      });

      // Re-enable button after 30 minutes
      setTimeout(() => {
        button.disabled = false;
        button.textContent = "📨 Send SMS Notification";
        removeCooldownMessage();
      }, 1800000); // 1800000ms = 30 minutes
    });

    function showToast(message) {
      const toast = document.createElement("div");
      toast.className = "toast";
      toast.textContent = message;
      document.querySelector(".container").appendChild(toast);

      setTimeout(() => {
        toast.remove();
      }, 5000);
    }

    function showCooldownMessage(msg) {
      removeCooldownMessage(); // Clear previous message if any
      const notice = document.createElement("p");
      notice.className = "cooldown";
      notice.id = "cooldownMsg";
      notice.textContent = msg;
      document.querySelector(".container").appendChild(notice);
    }

    function removeCooldownMessage() {
      const existing = document.getElementById("cooldownMsg");
      if (existing) existing.remove();
    }
  </script>

</body>
</html>