package com.codbid.telemetry.web;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;

import java.io.IOException;
import java.io.PrintWriter;

public class ResponseWrapper extends HttpServletResponseWrapper {

    private long contentSize = 0;

    public ResponseWrapper(HttpServletResponse response) {
        super(response);
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        ServletOutputStream original = super.getOutputStream();

        return new ServletOutputStream() {
            @Override
            public void write(int b) throws IOException {
                contentSize++;
                original.write(b);
            }

            @Override
            public boolean isReady() {
                return original.isReady();
            }

            @Override
            public void setWriteListener(jakarta.servlet.WriteListener writeListener) {
                original.setWriteListener(writeListener);
            }
        };
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        PrintWriter original = super.getWriter();

        return new PrintWriter(original) {
            @Override
            public void write(int c) {
                contentSize++;
                super.write(c);
            }

            @Override
            public void write(char[] buf, int off, int len) {
                contentSize += len;
                super.write(buf, off, len);
            }

            @Override
            public void write(String s, int off, int len) {
                contentSize += len;
                super.write(s, off, len);
            }
        };
    }

    public long getContentSize() {
        return contentSize;
    }
}