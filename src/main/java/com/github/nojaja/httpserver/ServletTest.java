package com.github.nojaja.httpserver;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.servlet.*;
import javax.servlet.http.*;

public class ServletTest extends HttpServlet {

	private static String workspace = "";
	{
		/*
		 * 環境変数workspaceからルートフォルダを取得する
		 */
		workspace = System.getenv("workspace");
		System.out.println(workspace);
		workspace = "D:\\devs\\workspace_node\\node-htmlcompiler\\tests\\react";
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * すべてのメソッドを許可する
	 */
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		//targetのパス取得
		String requestPath = request.getRequestURI().substring(request.getContextPath().length());
		//指定がない場合はindex.html
		if(requestPath.equals("/")) requestPath = "/index.html";
		
		/*
		 * このあたりでURLとのマッピング処理をする
		 */
		
		requestPath = workspace + requestPath;
		
		//パスの検証、ディレクトリ・トラバーサル対策
		File file = new File(requestPath);
		requestPath = file.getCanonicalPath();
		if(!requestPath.startsWith(workspace)){//workspaceより上位へのアクセスを防止
			notfound(request,response);
		}
		
		System.out.println("requestPath:"+requestPath);
		
		loadfile(request, response,requestPath);
	}

	/**
	 * 404画面
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	private void notfound(HttpServletRequest request,
			HttpServletResponse response)
					throws ServletException, IOException {

		// not exist
		response.setContentType("text/plain");
		response.getWriter().println("File not found...");
	}

	/**
	 * 対象ファイル返却処理
	 * @param request
	 * @param response
	 * @param requestPath
	 * @throws ServletException
	 * @throws IOException
	 */
	private void loadfile(HttpServletRequest request,
			HttpServletResponse response,String requestPath)
					throws ServletException, IOException {

		System.out.println("target path is ... : " + requestPath);

		// resource file
		InputStream is = null;
		try{
			is = Files.newInputStream(Paths.get(requestPath));
			// check: is file avalable?
			if (is == null) {
				// not exist
				notfound(request,response);
			} else {
				// file exist
				int filesize = is.available();
				// set response header
				setResponseHeader(response, requestPath, filesize);
				// output
				OutputStream out = response.getOutputStream();
				int b = 0;
				while ((b = is.read()) != -1) {
					out.write(b);
				}
			}

		} finally{
			if(is != null){
				is.close();
			}
		}

	}

	/**
	 * コンテンツタイプの設定
	 * Contentの種類検出と設定を行う
	 * ついでにキャッシュ設定もする
	 * @param response
	 * @param filename
	 * @param fileLength
	 */
	private void setResponseHeader(final HttpServletResponse response,
			String filename, final int fileLength) {

		//TODO context.xmlとか参照したほうが良い
		if (filename.endsWith(".html")) {
			response.setContentType("text/html");
		} else if (filename.endsWith(".xml")) {
			response.setContentType("application/xml");
		} else if (filename.endsWith(".js")) {
			response.setContentType("text/js");
		} else if (filename.endsWith(".css")) {
			response.setContentType("text/css");
		} else if (filename.endsWith(".json")) {
			response.setContentType("text/json");


		} else if (filename.endsWith(".pdf")) {
			// PDF
			response.setContentType("application/pdf");
		} else if (filename.endsWith(".xls")) {
			// Excel
			response.setContentType("application/vnd.ms-excel");
		} else if (filename.endsWith(".xml")) {
			// XML
			response.setContentType("text/xml");
		} else if (filename.endsWith(".jad")) {
			// JAD
			response.setContentType("text/vnd.sun.j2me.app-descriptor");
		} else if (filename.endsWith(".cod")) {
			// COD
			response.setContentType("application/vnd.rim.cod");
		} else if (filename.endsWith(".ogg")) {
			// OGG
			response.setContentType("video/ogg");
		} else if (filename.endsWith(".ogv")) {
			// OGV
			response.setContentType("video/ogg");
		} else if (filename.endsWith(".mp4")) {
			// MP4
			response.setContentType("video/mp4");
		} else if (filename.endsWith(".webm")) {
			// WEBM
			response.setContentType("video/webm");
		} else {
			// other is binary
			response.setContentType("application/octet-stream");
		}

		if (fileLength > 0) {
			// Length
			response.setContentLength(fileLength);
		}
		// Disable cache
		response.addHeader("Cache-Control", "no-cache");
		response.addHeader("Pragma", "no-cache");
		response.addHeader("Expires", "0");
	}

}