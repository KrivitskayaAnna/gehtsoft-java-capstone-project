const express = require("express");
const cors = require("cors");
const { createProxyMiddleware } = require("http-proxy-middleware");
const path = require("path");

const app = express();
const PORT = 3002;

// Enable CORS for all routes
app.use(cors());

// Serve static files from the current directory
app.use(express.static(__dirname));

// Proxy API requests to your backend
app.use(
  "/api",
  createProxyMiddleware({
    target: "http://localhost:8080",
    changeOrigin: true,
    pathRewrite: {
      "^/api": "/api", // keep the /api prefix
    },
    onError: (err, req, res) => {
      console.error("Proxy Error:", err);
      res.status(500).json({ error: "Backend server is not available" });
    },
  })
);

// // For all other routes, serve the index.html (SPA support)
// app.get("*", (req, res) => {
//   res.sendFile(path.join(__dirname, "form_page/form.html"));
// });

app.listen(PORT, () => {
  console.log(
    `🎯 Frontend server running on http://localhost:${PORT}, with entrypoint on /form_page/form.html`
  );
  console.log(`🔄 Proxying API requests to http://localhost:8080`);
  console.log(`📁 Serving static files from: ${__dirname}`);
});
