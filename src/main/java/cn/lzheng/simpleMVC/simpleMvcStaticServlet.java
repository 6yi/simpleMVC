package cn.lzheng.simpleMVC;

import org.apache.catalina.connector.ResponseFacade;
import org.apache.catalina.servlets.DefaultServlet;
import org.apache.naming.resources.CacheEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletResponse;
import javax.servlet.ServletResponseWrapper;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
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
        this.serveResource(request, response, true);
    }

    @Override
    protected void serveResource(HttpServletRequest request, HttpServletResponse response, boolean content) throws IOException, ServletException {
        boolean serveContent = content;
        String path = (String) request.getAttribute("url");
        logger.debug("path:---"+path);
        if (this.debug > 0) {
            if (content) {
                this.log("DefaultServlet.serveResource:  Serving resource '" + path + "' headers and data");
            } else {
                this.log("DefaultServlet.serveResource:  Serving resource '" + path + "' headers only");
            }
        }

        CacheEntry cacheEntry = this.resources.lookupCache(path);
        String requestUri;
        if (!cacheEntry.exists){
            requestUri = (String)request.getAttribute("url");
            if (requestUri == null) {
                requestUri = request.getRequestURI();
                response.sendError(404, requestUri);
            } else {
                throw new FileNotFoundException(sm.getString("defaultServlet.missingResource", new Object[]{requestUri}));
            }
        } else if (cacheEntry.context == null && (path.endsWith("/") || path.endsWith("\\"))) {
            requestUri = (String)request.getAttribute("url");
            if (requestUri == null) {
                requestUri = request.getRequestURI();
            }

            response.sendError(404, requestUri);
        } else {
            boolean isError = response.getStatus() >= 400;
            if (cacheEntry.context == null) {
                boolean included = request.getAttribute("javax.servlet.include.context_path") != null;
                if (!included && !isError && !this.checkIfHeaders(request, response, cacheEntry.attributes)) {
                    return;
                }
            }

            String contentType = cacheEntry.attributes.getMimeType();
            if (contentType == null) {
                contentType = this.getServletContext().getMimeType(cacheEntry.name);
                cacheEntry.attributes.setMimeType(contentType);
            }

            ArrayList<Range> ranges = null;
            long contentLength = -1L;
            if (cacheEntry.context != null) {
                if (!this.listings) {
                    response.sendError(404, request.getRequestURI());
                    return;
                }

                contentType = "text/html;charset=UTF-8";
            } else {
                if (!isError) {
                    if (this.useAcceptRanges) {
                        response.setHeader("Accept-Ranges", "bytes");
                    }

                    ranges = this.parseRange(request, response, cacheEntry.attributes);
                    response.setHeader("ETag", cacheEntry.attributes.getETag());
                    response.setHeader("Last-Modified", cacheEntry.attributes.getLastModifiedHttp());
                }

                contentLength = cacheEntry.attributes.getContentLength();
                if (contentLength == 0L) {
                    serveContent = false;
                }
            }

            ServletOutputStream ostream = null;
            PrintWriter writer = null;
            if (serveContent) {
                try {
                    ostream = response.getOutputStream();
                } catch (IllegalStateException var24) {
                    if (contentType != null && !contentType.startsWith("text") && !contentType.endsWith("xml") && !contentType.contains("/javascript")) {
                        throw var24;
                    }

                    writer = response.getWriter();
                    ranges = FULL;
                }
            }

            ServletResponse r = response;

            long contentWritten;
            for(contentWritten = 0L; r instanceof ServletResponseWrapper; r = ((ServletResponseWrapper)r).getResponse()) {
            }

            if (r instanceof ResponseFacade) {
                contentWritten = ((ResponseFacade)r).getContentWritten();
            }

            if (contentWritten > 0L) {
                ranges = FULL;
            }

            if (cacheEntry.context == null && !isError && (ranges != null && !ranges.isEmpty() || request.getHeader("Range") != null) && ranges != FULL) {
                if (ranges == null || ranges.isEmpty()) {
                    return;
                }

                response.setStatus(206);
                if (ranges.size() == 1) {
                    DefaultServlet.Range range = (DefaultServlet.Range)ranges.get(0);
                    response.addHeader("Content-Range", "bytes " + range.start + "-" + range.end + "/" + range.length);
                    long length = range.end - range.start + 1L;
                    if (length < 2147483647L) {
                        response.setContentLength((int)length);
                    } else {
                        response.setHeader("content-length", "" + length);
                    }

                    if (contentType != null) {
                        if (this.debug > 0) {
                            this.log("DefaultServlet.serveFile:  contentType='" + contentType + "'");
                        }

                        response.setContentType(contentType);
                    }

                    if (serveContent) {
                        try {
                            response.setBufferSize(this.output);
                        } catch (IllegalStateException var22) {
                        }

                        if (ostream == null) {
                            throw new IllegalStateException();
                        }

                        if (!this.checkSendfile(request, response, cacheEntry, range.end - range.start + 1L, range)) {
                            this.copy(cacheEntry, ostream, range);
                        }
                    }
                } else {
                    response.setContentType("multipart/byteranges; boundary=CATALINA_MIME_BOUNDARY");
                    if (serveContent) {
                        try {
                            response.setBufferSize(this.output);
                        } catch (IllegalStateException var21) {
                        }

                        if (ostream == null) {
                            throw new IllegalStateException();
                        }

                        this.copy(cacheEntry, ostream, ranges.iterator(), contentType);
                    }
                }
            } else {
                if (contentType != null) {
                    if (this.debug > 0) {
                        this.log("DefaultServlet.serveFile:  contentType='" + contentType + "'");
                    }

                    response.setContentType(contentType);
                }

                if (cacheEntry.resource != null && contentLength >= 0L && (!serveContent || ostream != null)) {
                    if (this.debug > 0) {
                        this.log("DefaultServlet.serveFile:  contentLength=" + contentLength);
                    }

                    if (contentWritten == 0L) {
                        if (contentLength < 2147483647L) {
                            response.setContentLength((int)contentLength);
                        } else {
                            response.setHeader("content-length", "" + contentLength);
                        }
                    }
                }

                InputStream renderResult = null;
                if (cacheEntry.context != null && serveContent) {
                    renderResult = this.render(this.getPathPrefix(request), cacheEntry);
                }

                if (serveContent) {
                    try {
                        response.setBufferSize(this.output);
                    } catch (IllegalStateException var23) {
                    }

                    if (ostream != null) {
                        if (!this.checkSendfile(request, response, cacheEntry, contentLength, (DefaultServlet.Range)null)) {
                            this.copy(cacheEntry, renderResult, ostream);
                        }
                    } else {
                        this.copy(cacheEntry, renderResult, writer);
                    }
                }
            }

        }
    }

}
