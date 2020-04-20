package ie.gmit.sw;

import ie.gmit.sw.language_detector.LangDetector;
import ie.gmit.sw.language_detector.LangDetectorFactory;
import ie.gmit.sw.language_detector_system.LangDetectionSystem;
import ie.gmit.sw.language_distribution.LangDistStore;
import ie.gmit.sw.language_distribution.LangDistStoreBuilder;
import ie.gmit.sw.sample_parser.FileSampleParser;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

/**
 * Servlet for the asynchronous language detection system.
 */
public class ServiceHandler extends HttpServlet {
	private String languageDataSet = null; //This variable is shared by all HTTP requests for the servlet
	private static long jobNumber = 0; //The number of the task in the async queue

	private File f;

	private LangDetectionSystem langDetectionSystem;
	private LangDetector langDetector;

	/**
	 * Initialises servlet and language detection system.
	 */
	public void init() {
		ServletContext ctx = getServletContext(); //Get a handle on the application context
		languageDataSet = ctx.getInitParameter("LANGUAGE_DATA_SET"); //Reads the value from the <context-param> in web.xml

		f = new File(languageDataSet);

		// build k-mer distribution for all languages from language dataset
		LangDistStore distStore = new LangDistStoreBuilder()
			.withMappedStore(512, 3)
			.registerParser(
				new FileSampleParser(f)
			)
		.build();

		// create language detection system
		langDetector = LangDetectorFactory.getInstance().getLanguageDetector("Out-of-place");
		langDetectionSystem = new LangDetectionSystem(distStore, langDetector,50, 4);
		// start workers
		langDetectionSystem.go();
	}

	/**
	 * Responds to GET requests on this servlet by queueing, processing, and displaying the result of a language
	 * detection job.
	 */
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		resp.setContentType("text/html"); //Output the MIME type
		// support for characters such as Chinese
		req.setCharacterEncoding("UTF-8");
		resp.setCharacterEncoding("UTF-8");
		PrintWriter out = resp.getWriter(); //Write out text. We can write out binary too and change the MIME type...

		//Initialise some request variables with the submitted form info. These are local to this method and thread safe...
		String metricOption = req.getParameter("cmbOptions");
		String query = req.getParameter("query");
		String taskNumber = req.getParameter("frmTaskNumber");

		out.print("<!DOCTYPE html><html><head><title>Advanced Object Oriented Software Development Assignment</title>");
		out.print("<meta charset=\"UTF-8\">");
		out.print("</head>");
		out.print("<body>");
		out.print("<div id=\"r\"></div>");

		// ensure language dataset file was found
		if (f.exists()) {
			out.print("<font color=\"#993333\">");
			out.print("Language Dataset is located at " + languageDataSet + " and is <u>" + f.length() + "</u> bytes in size");
			out.print("<br><br>Distance metric: " + metricOption);
			out.print("<br><br>Query Text: " + query);
			out.print("</font><br>");

			if (taskNumber == null) {
				// job not yet submitted
				// create task number
				taskNumber = String.format("T%d", jobNumber++);

				// switch to the selected language detection algorithm
				langDetector.switchToStrategy(metricOption);

				// submit job to the system for processing by a worker
				langDetectionSystem.submitJob(taskNumber, query);
			}

			if (langDetectionSystem.isJobFinished(taskNumber)) {
				// job finished, display detected language
				out.printf("<h2>STATUS: Job %s complete</h2>", taskNumber);
				out.printf("<h2>Detected language: <font color=\"#00ab00\">%s</font></h2>", langDetectionSystem.getLanguageResult(taskNumber));
			}
			else {
				// job still processing
				out.printf("<h2>STATUS: Processing request for Job#: %s, please wait...</h2>", taskNumber);
				out.print("<script>");
				out.print("var wait=setTimeout(\"document.frmRequestDetails.submit();\", 5000);");
				out.print("</script>");
			}

			// resubmit form options
			out.print("<form method=\"POST\" name=\"frmRequestDetails\">");
			out.print("<input name=\"cmbOptions\" type=\"hidden\" value=\"" + metricOption + "\">");
			out.print("<input name=\"query\" type=\"hidden\" value=\"" + query + "\">");
			out.print("<input name=\"frmTaskNumber\" type=\"hidden\" value=\"" + taskNumber + "\">");
			out.print("</form>");
		}
		else {
			// dataset file not found
			out.print("<font color=\"#FF2222\"><b>");
			out.print("Language Dataset was not found at " + languageDataSet);
			out.print("</b></font><font color=\"#993333\">");
			out.print("<br>Place the file in the above location and reload tomcat.</font>");
		}

		out.print("</body></html>");
	}

	/**
	 * Responds to a POST request on this servlet. Passes the request and response on to
	 * the GET method handler.
	 */
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		doGet(req, resp);
 	}
}