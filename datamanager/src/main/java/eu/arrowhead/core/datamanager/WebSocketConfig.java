/********************************************************************************
 * Copyright (c) 2020 {Lulea University of Technology}
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 
 *
 * Contributors: 
 *   {Lulea University of Technology} - implementation
 *   Arrowhead Consortia - conceptualization 
 ********************************************************************************/
package eu.arrowhead.core.datamanager;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import eu.arrowhead.core.datamanager.HistorianWSHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import eu.arrowhead.common.CommonConstants;
import eu.arrowhead.common.CoreCommonConstants;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    //=================================================================================================
	// members

	private static final Logger logger = LogManager.getLogger(WebSocketConfig.class);
 
    @Value("${websockets.enabled}")
    private boolean websocketsEnabled;

    @Autowired
    HistorianWSHandler historianWSHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry webSocketHandlerRegistry) {
            if (websocketsEnabled) {
                logger.info("WebSockets is enabled, initializing...");
                webSocketHandlerRegistry.addHandler(historianWSHandler, "/ws/datamanager/historian/*/*").addInterceptors(historianInterceptor());
            }
    }

    @Bean
    public HandshakeInterceptor historianInterceptor() {
        return new HandshakeInterceptor() {
            public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, 
                  WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {

                // Get the URI segment corresponding to the endpoints during handshake
                String path = request.getURI().getPath();
                //System.out.println("PATH: " + path);
                final String serviceName = path.substring(path.lastIndexOf('/') + 1);
                path = path.substring(0, path.lastIndexOf('/'));
                final String systemName = path.substring(path.lastIndexOf('/') + 1);

                logger.debug("Target: " + systemName + "/" + serviceName);

                // This will be added to the websocket session
                String CN = ""; //XXX add me when using TLS
                attributes.put("CN", CN);
                attributes.put("systemName", systemName);
                attributes.put("serviceName", serviceName);
                return true;
            }

            public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
                // Nothing to do after handshake
            }
        };
    }

}
