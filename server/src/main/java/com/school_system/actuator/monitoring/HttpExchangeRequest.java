package com.school_system.actuator.monitoring;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.net.URI;
import java.util.List;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class HttpExchangeRequest{
    private  URI uri;
    private  String remoteAddress;
    private  String method;
    private  Map<String, List<String>> headers;
}
