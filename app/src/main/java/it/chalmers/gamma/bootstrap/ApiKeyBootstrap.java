package it.chalmers.gamma.bootstrap;

import it.chalmers.gamma.app.apikey.domain.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ApiKeyBootstrap {

  private static final Logger LOGGER = LoggerFactory.getLogger(ApiKeyBootstrap.class);

  private final MockData mockData;
  private final ApiKeyRepository apiKeyRepository;
  private final ApiKeySettingsRepository apiKeySettingsRepository;
  private final BootstrapSettings bootstrapSettings;

  public ApiKeyBootstrap(MockData mockData, ApiKeyRepository apiKeyRepository, ApiKeySettingsRepository apiKeySettingsRepository, BootstrapSettings bootstrapSettings) {
    this.mockData = mockData;
    this.apiKeyRepository = apiKeyRepository;
    this.apiKeySettingsRepository = apiKeySettingsRepository;
    this.bootstrapSettings = bootstrapSettings;
  }

  public void ensureApiKeys() {
    if (!this.bootstrapSettings.mocking() || !this.apiKeyRepository.getAll().isEmpty()) {
      return;
    }

    LOGGER.info("========== API BOOTSTRAP ==========");
    for (ApiKeyType apiKeyType : ApiKeyType.values()) {
      if (apiKeyType != ApiKeyType.CLIENT) {

        var createdApiKey =
            this.apiKeyFacade.create(
                new ApiKeyFacade.NewApiKey(
                    apiKeyType.name().toLowerCase() + "-mock", "", "", apiKeyType.name()));

        LOGGER.info(
            "Api key of type "
                + apiKeyType.name()
                + " has been generated with id: "
                + createdApiKey.apiKey().id()
                + " and code: "
                + createdApiKey.token());
      }
    }

    LOGGER.info("Add the header: Authorization: pre-shared <ID>:<TOKEN> to start using the APIs");
    LOGGER.info("==========               ==========");
  }

  private ApiKey toApiKey(MockData.MockApiKey mockApiKey) {
    new ApiKey(
        new ApiKeyId(mockApiKey.id()),
        new PrettyName(mockApiKey.prettyName()),
        new Text(mockApiKey.description().sv(), mockApiKey.description().en()),
        mockApiKey.type(),
        new ApiKeyToken(this.passwordEncoder.encode(mockApiKey.token())));
  }
}
