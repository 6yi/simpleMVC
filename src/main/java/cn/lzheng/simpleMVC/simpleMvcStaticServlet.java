package cn.lzheng.simpleMVC;

import org.apache.catalina.connector.ResponseFacade;
import org.apache.catalina.servlets.DefaultServlet;
import org.apache.naming.resources.CacheEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;

/**
 * @ClassName simpleMvcStaticServlet
 * @Author 刘正
 * @Date 2020/9/26 13:11
 * @Version 1.0
 * @Description:
 */

public class simpleMvcStaticServlet extends DefaultServlet {
    private static Logger logger = LoggerFactory.getLogger(simpleMvcStaticServlet.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
       serveResource(request,response,true);
    }

    @Override
    protected void serveResource(HttpServletRequest request, HttpServletResponse response, boolean content) throws IOException, ServletException {
        boolean serveContent = content;
        // Identify the requested resource path
        String path = (String) request.getAttribute("url");
        if (debug > 0) {
            if (serveContent)
                log("DefaultServlet.serveResource:  Serving resource '" +
                        path + "' headers and data");
            else
                log("DefaultServlet.serveResource:  Serving resource '" +
                        path + "' headers only");
        }

        CacheEntry cacheEntry = resources.lookupCache(path);

        if (!cacheEntry.exists) {
            // Check if we're included so we can return the appropriate
            // missing resource name in the error
            String requestUri = (String) request.getAttribute(
                    RequestDispatcher.INCLUDE_REQUEST_URI);
            if (requestUri == null) {
                requestUri = request.getRequestURI();
            } else {
                // We're included
                // SRV.9.3 says we must throw a FNFE
                throw new FileNotFoundException(
                        sm.getString("defaultServlet.missingResource",
                                requestUri));
            }

            response.sendError(HttpServletResponse.SC_NOT_FOUND,
                    requestUri);
            return;
        }

        // If the resource is not a collection, and the resource path
        // ends with "/" or "\", return NOT FOUND
        if (cacheEntry.context == null) {
            if (path.endsWith("/") || (path.endsWith("\\"))) {
                // Check if we're included so we can return the appropriate
                // missing resource name in the error
                String requestUri = (String) request.getAttribute(
                        RequestDispatcher.INCLUDE_REQUEST_URI);
                if (requestUri == null) {
                    requestUri = request.getRequestURI();
                }
                response.sendError(HttpServletResponse.SC_NOT_FOUND,
                        requestUri);
                return;
            }
        }

        boolean isError =
                response.getStatus() >= HttpServletResponse.SC_BAD_REQUEST;

        // Check if the conditions specified in the optional If headers are
        // satisfied.
        if (cacheEntry.context == null) {

            // Checking If headers
            boolean included = (request.getAttribute(
                    RequestDispatcher.INCLUDE_CONTEXT_PATH) != null);
            if (!included && !isError &&
                    !checkIfHeaders(request, response, cacheEntry.attributes)) {
                return;
            }

        }

        // Find content type.
        String contentType = cacheEntry.attributes.getMimeType();
        if (contentType == null) {
            contentType = getServletContext().getMimeType(cacheEntry.name);
            cacheEntry.attributes.setMimeType(contentType);
        }

        ArrayList<Range> ranges = null;
        long contentLength = -1L;

        if (cacheEntry.context != null) {

            // Skip directory listings if we have been configured to
            // suppress them
            if (!listings) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND,
                        request.getRequestURI());
                return;
            }
            contentType = "text/html;charset=UTF-8";

        } else {
            if (!isError) {
                if (useAcceptRanges) {
                    // Accept ranges header
                    response.setHeader("Accept-Ranges", "bytes");
                }

                // Parse range specifier
                ranges = parseRange(request, response, cacheEntry.attributes);

                // ETag header
                response.setHeader("ETag", cacheEntry.attributes.getETag());

                // Last-Modified header
                response.setHeader("Last-Modified",
                        cacheEntry.attributes.getLastModifiedHttp());
            }

            // Get content length
            contentLength = cacheEntry.attributes.getContentLength();
            // Special case for zero length files, which would cause a
            // (silent) ISE when setting the output buffer size
            if (contentLength == 0L) {
                serveContent = false;
            }

        }

        ServletOutputStream ostream = null;
        PrintWriter writer = null;

        if (serveContent) {

            // Trying to retrieve the servlet output stream

            try {
                ostream = response.getOutputStream();
            } catch (IllegalStateException e) {
                // If it fails, we try to get a Writer instead if we're
                // trying to serve a text file
                if ( (contentType == null)
                        || (contentType.startsWith("text"))
                        || (contentType.endsWith("xml"))
                        || (contentType.contains("/javascript")) ) {
                    writer = response.getWriter();
                    // Cannot reliably serve partial content with a Writer
                    ranges = FULL;
                } else {
                    throw e;
                }
            }

        }

        // Check to see if a Filter, Valve of wrapper has written some content.
        // If it has, disable range requests and setting of a content length
        // since neither can be done reliably.
        ServletResponse r = response;
        long contentWritten = 0;
        while (r instanceof ServletResponseWrapper) {
            r = ((ServletResponseWrapper) r).getResponse();
        }
        if (r instanceof ResponseFacade) {
            contentWritten = ((ResponseFacade) r).getContentWritten();
        }
        if (contentWritten > 0) {
            ranges = FULL;
        }

        if ( (cacheEntry.context != null)
                || isError
                || ( ((ranges == null) || (ranges.isEmpty()))
                && (request.getHeader("Range") == null) )
                || (ranges == FULL) ) {

            // Set the appropriate output headers
            if (contentType != null) {
                if (debug > 0)
                    log("DefaultServlet.serveFile:  contentType='" +
                            contentType + "'");
                response.setContentType(contentType);
            }
            if ((cacheEntry.resource != null) && (contentLength >= 0)
                    && (!serveContent || ostream != null)) {
                if (debug > 0)
                    log("DefaultServlet.serveFile:  contentLength=" +
                            contentLength);
                // Don't set a content length if something else has already
                // written to the response.
                if (contentWritten == 0) {
                    if (contentLength < Integer.MAX_VALUE) {
                        response.setContentLength((int) contentLength);
                    } else {
                        // Set the content-length as String to be able to use a
                        // long
                        response.setHeader("content-length",
                                "" + contentLength);
                    }
                }
            }

            InputStream renderResult = null;
            if (cacheEntry.context != null) {

                if (serveContent) {
                    // Serve the directory browser
                    renderResult = render(getPathPrefix(request), cacheEntry);
                }

            }

            // Copy the input stream to our output stream (if requested)
            if (serveContent) {
                try {
                    response.setBufferSize(output);
                } catch (IllegalStateException e) {
                    // Silent catch
                }
                if (ostream != null) {
                    if (!checkSendfile(request, response, cacheEntry, contentLength, null))
                        copy(cacheEntry, renderResult, ostream);
                } else {
                    copy(cacheEntry, renderResult, writer);
                }
            }

        } else {

            if ((ranges == null) || (ranges.isEmpty()))
                return;

            // Partial content response.

            response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);

            if (ranges.size() == 1) {

                Range range = ranges.get(0);
                response.addHeader("Content-Range", "bytes "
                        + range.start
                        + "-" + range.end + "/"
                        + range.length);
                long length = range.end - range.start + 1;
                if (length < Integer.MAX_VALUE) {
                    response.setContentLength((int) length);
                } else {
                    // Set the content-length as String to be able to use a long
                    response.setHeader("content-length", "" + length);
                }

                if (contentType != null) {
                    if (debug > 0)
                        log("DefaultServlet.serveFile:  contentType='" +
                                contentType + "'");
                    response.setContentType(contentType);
                }

                if (serveContent) {
                    try {
                        response.setBufferSize(output);
                    } catch (IllegalStateException e) {
                        // Silent catch
                    }
                    if (ostream != null) {
                        if (!checkSendfile(request, response, cacheEntry, range.end - range.start + 1, range))
                            copy(cacheEntry, ostream, range);
                    } else {
                        // we should not get here
                        throw new IllegalStateException();
                    }
                }

            } else {

                response.setContentType("multipart/byteranges; boundary="
                        + mimeSeparation);

                if (serveContent) {
                    try {
                        response.setBufferSize(output);
                    } catch (IllegalStateException e) {
                        // Silent catch
                    }
                    if (ostream != null) {
                        copy(cacheEntry, ostream, ranges.iterator(),
                                contentType);
                    } else {
                        // we should not get here
                        throw new IllegalStateException();
                    }
                }

            }

        }
    }
}
