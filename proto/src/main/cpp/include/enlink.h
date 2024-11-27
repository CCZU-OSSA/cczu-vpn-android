// namespace enlink


extern "C" const char *version();
extern "C" bool start_service(const char *, const char *);
extern "C" char *proxy_server();
extern "C" bool send_packet(const char *, unsigned int);
extern "C" bool send_tcp_packet(const char *, unsigned int);
extern "C" bool send_heartbeat();
extern "C" char *receive_packet(unsigned int);
extern "C" bool service_available();
extern "C" bool stop_service();
extern "C" bool webvpn_available();
extern "C" void free_memory(char *);