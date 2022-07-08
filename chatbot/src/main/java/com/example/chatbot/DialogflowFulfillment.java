package com.example.chatbot;

import com.google.rpc.context.AttributeContext;
import org.apache.http.HttpException;
import org.apache.http.client.HttpResponseException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

public class DialogflowFulfillment {
    // To hold all related info for each unique user
    static JSONObject userInfo = new JSONObject();

    //List of business activities that are marked as excluded
    List<String> excludedBizActivities = new ArrayList<>();
    RestTemplate restTemplate = new RestTemplate();
    RestTemplate restTemplateCustomerId = new RestTemplate();
    RestTemplate restTemplateProposalId = new RestTemplate();
    RestTemplate restTemplateFullUpdate = new RestTemplate();
    TokenManagement tokenManagement = new TokenManagement();
    HttpHeaders headers = new HttpHeaders();
    HttpHeaders CustomerAuthHeaders = AuthHeadersManagement.customerAuthHeaders();
    HttpHeaders ProposalAuthHeaders = AuthHeadersManagement.proposalAuthHeaders();
    HttpHeaders FullUpdateAuthHeaders = AuthHeadersManagement.fullUpdateHeaders();
    public JSONObject fulfillment(JSONObject payload) {

        // Parse the payload to retrieve the relevant information
        String sessionID = payload.getString("session"); // To hold the unique sessionID from dialogflow
        String intent = payload.getJSONObject("queryResult").getJSONObject("intent").getString("displayName"); // To extract the intent of the user input from the dialogflow request
        JSONObject params = payload.getJSONObject("queryResult").getJSONObject("parameters");
        String user = sessionID.split("/")[sessionID.split("/").length-1];

        // Create unique context objects based on each sessionID
        JSONArray restartContext = new JSONArray("[{\"name\":\"" + sessionID + "/contexts/restart\", \"lifespanCount\": 2}]");
        JSONArray quoteContext = new JSONArray("[{\"name\":\"" + sessionID + "/contexts/quote\", \"lifespanCount\": 2}]");
        JSONArray turnoverContext = new JSONArray("[{\"name\":\"" + sessionID + "/contexts/turnover\", \"lifespanCount\": 2}]");
        JSONArray exposureContext = new JSONArray("[{\"name\":\"" + sessionID + "/contexts/exposure\", \"lifespanCount\": 2}]");
        JSONArray nameContext = new JSONArray("[{\"name\":\"" + sessionID + "/contexts/name\", \"lifespanCount\": 99}]");

        // To be returned to dialogflow to respond to user. Refer to https://cloud.google.com/dialogflow/es/docs/reference/rest/v2/WebhookResponse to view the correct format to respond to dialogflow
        JSONObject fulfillment = new JSONObject();

        switch (intent) {
            case "BA":
            case "Exposure":

                // Check if the given business activity is one of the excluded ones
                if (excludedBizActivities.contains(params.getString(intent))) {
                    fulfillment.put("outputContexts", restartContext);
                    fulfillment.put("fulfillmentText", "Your application has been rejected.\nIn case of any questions or inquiries, please contact us:\nEmail: no-reply@brokerabc.com\nPhone: 8888 8888.\n\nTo start over, type \"restart\". Good bye!");
                    return fulfillment;
                }

                int percentage = Integer.parseInt(params.getString("percentage").substring(0, params.getString("percentage").lastIndexOf("%")));

                // If new user, add mappings for Business Activity, Exposure, BizActs (String of users input business activities), Exp (String of users input exposure)
                if (!userInfo.has(user)) {
                    userInfo.put(user, new JSONObject("{\"BA\":0,\"Exposure\":0, \"BizActs\":\"\", \"Exp\":\"\", \"CustomerId\":\"\", \"ProposalId\":\"\"}"));
                }
                try{
//                    System.out.println(headers.getFirst("Authorization"));
                    String tok = "eyJhbGciOiJIUzUxMiJ9.eyJhdWQiOiJodHRwczovL2RjbXAtZGV2LmRpc2NvdmVybWFya2V0LmNvbSIsInN1YiI6IjAwMDAwMDAwYWIxMDgzMjMwYTY1MzlkNCIsImNvZGUiOiJzdHJpbmciLCJwZXJtaXNzaW9ucyI6WyJkZXBlbmRhbnRzLmNyZWF0ZS5hbnkiLCJkZXBlbmRhbnRzLnJlYWQuYW55IiwiZGVwZW5kYW50cy51cGRhdGUuYW55IiwiZGVwZW5kYW50cy5kZWxldGUuYW55IiwicHJvcG9zYWxzLmNyZWF0ZS50ZW5hbnQiLCJ0ZW5hbnRzLnJlYWQub3duIiwicHJvcG9zYWxzLnVwZGF0ZS50ZW5hbnQiLCJwcm9wb3NhbHMucmVhZC50ZW5hbnQiLCJwcm9wb3NhbHMuZGVsZXRlLnRlbmFudCIsImN1c3RvbWVycy5jcmVhdGUudGVuYW50IiwiY3VzdG9tZXJzLnJlYWQudGVuYW50IiwiY3VzdG9tZXJzLnVwZGF0ZS50ZW5hbnQiLCJjdXN0b21lcnMuZGVsZXRlLnRlbmFudCIsImN1c3RvbWVycy5yZWFkLm93biIsInNjYW4uY3JlYXRlLnRlbmFudCIsInNjYW4ucmVhZC50ZW5hbnQiLCJ1c2Vycy5yZWFkLm93biIsInVzZXJzLnVwZGF0ZS5vd24iLCJ1c2Vycy5jcmVhdGUuYW55IiwiY3VzdG9tZXJzLnJlYWQudGVuYW50IiwiY3VzdG9tZXJzLmNyZWF0ZS50ZW5hbnQiLCJjdXN0b21lcnMudXBkYXRlLnRlbmFudCIsImN1c3RvbWVycy5kZWxldGUudGVuYW50IiwicHJvcG9zYWxzLmNyZWF0ZS50ZW5hbnQiLCJwcm9wb3NhbHMudXBkYXRlLnRlbmFudCIsInByb3Bvc2Fscy5yZWFkLnRlbmFudCIsInByb3Bvc2Fscy5kZWxldGUudGVuYW50IiwidXNlcnMucmVhZC5vd24iLCJ1c2Vycy51cGRhdGUub3duIiwidGVuYW50cy5yZWFkLm93biIsInRlbmFudHMudXBkYXRlLm93biIsInVzZXJzLmNyZWF0ZS5hbnkiLCJjbGFpbXMucmVhZC50ZW5hbnQiLCJwb2xpY2llcy5yZWFkLnRlbmFudCIsImN1c3RvbWVycy5yZWFkLnRlbmFudCIsImN1c3RvbWVycy5jcmVhdGUudGVuYW50IiwiY3VzdG9tZXJzLnVwZGF0ZS50ZW5hbnQiLCJjdXN0b21lcnMuZGVsZXRlLnRlbmFudCIsInByb3Bvc2Fscy5jcmVhdGUudGVuYW50IiwicHJvcG9zYWxzLnVwZGF0ZS50ZW5hbnQiLCJwcm9wb3NhbHMucmVhZC50ZW5hbnQiLCJwcm9wb3NhbHMuZGVsZXRlLnRlbmFudCIsInVzZXJzLnJlYWQub3duIiwidXNlcnMudXBkYXRlLm93biIsInRlbmFudHMucmVhZC5vd24iLCJ0ZW5hbnRzLnVwZGF0ZS5vd24iLCJ1c2Vycy5jcmVhdGUuYW55IiwiY2xhaW1zLnJlYWQudGVuYW50IiwicG9saWNpZXMucmVhZC50ZW5hbnQiLCJkZXBlbmRhbnRzLmNyZWF0ZS5hbnkiLCJkZXBlbmRhbnRzLnJlYWQuYW55IiwiZGVwZW5kYW50cy51cGRhdGUuYW55IiwiZGVwZW5kYW50cy5kZWxldGUuYW55IiwicHJvcG9zYWxzLmNyZWF0ZS50ZW5hbnQiLCJ0ZW5hbnRzLnJlYWQub3duIiwicHJvcG9zYWxzLnVwZGF0ZS50ZW5hbnQiLCJwcm9wb3NhbHMucmVhZC50ZW5hbnQiLCJwcm9wb3NhbHMuZGVsZXRlLnRlbmFudCIsImN1c3RvbWVycy5jcmVhdGUudGVuYW50IiwiY3VzdG9tZXJzLnJlYWQudGVuYW50IiwiY3VzdG9tZXJzLnVwZGF0ZS50ZW5hbnQiLCJjdXN0b21lcnMuZGVsZXRlLnRlbmFudCIsImN1c3RvbWVycy5yZWFkLm93biIsInNjYW4uY3JlYXRlLnRlbmFudCIsInNjYW4ucmVhZC50ZW5hbnQiLCJ1c2Vycy5yZWFkLm93biIsInVzZXJzLnVwZGF0ZS5vd24iLCJ1c2Vycy5jcmVhdGUuYW55Il0sInJvbGVzIjpbIlJPMDAwMDAyNiIsIlJPMDAwMDAxOCIsIlJPMDAwMDAxOCIsIlJPMDAwMDAyNiJdLCJ0ZW5hbnRJZCI6IjYxOWM5ZDJlNGIwMjUzNDY1YTc5N2ZkMSIsImlzcyI6Imh0dHBzOi8vZGNtcC1kZXYuZGlzY292ZXJtYXJrZXQuY29tIiwidGVuYW50Q29kZSI6IlRFMDAwMDAwNSIsImV4cCI6MTY1NzI2Njk3OCwiaWF0IjoxNjU3MjYyNDc4LCJlbWFpbCI6IlRFMDAwMDAwNUBkaXNjb3Zlcm1hcmtldC5jb20ifQ.W3AJE5J7E126_5m5Wmm9kU-ulswR_jgzoIR7KKI0gEzN7msaOMDcHx7rkzPcjdLkkhaX_FEA4vGcv_yu00jIVQ";
                    //GetCustomerId
                    CustomerAuthHeaders.setBearerAuth(tok);
                    HttpEntity<String> httpEntityCustomer = new HttpEntity<>("{\"dateOfBirth\":null,\"dateOfDeath\":null,\"description\":\"\",\"email\":\"\"," +
                            "\"externalSourceId\":\"\",\"genderCode\":\"\",\"isoCountryCode\":\"\",\"isoResidenceCountryCode\":\"\",\"lastName\":\"\",\"maritalStatusCode\":\"NK\"," +
                            "\"mobile\":\"\",\"name\":\"Primary Insured\",\"companyRegistrationNumber\":\"\",\"companyRegistrationAuthority\":\"\",\"website\":\"\"," +
                            "\"personalInformationConsent\":true,\"communication\":[{\"communicationChannelName\":\"\",\"isPreferred\":true,\"communicationDetails\":" +
                            "[{\"locationType\":\"\",\"isPreferred\":true,\"blockNumber\":\"\",\"unitNumber\":\"\",\"addressLine1\":\"\",\"addressLine2\":\"\",\"isoCityCode\":\"\"," +
                            "\"isoCountryCode\":\"\",\"postalCode\":\"\",\"internationalTelecomCountryCode\":\"\",\"phoneNumber\":\"\",\"emailAddress\":\"\"}]}]" +
                            ",\"userLoginMethod\":\"Anonymous\"}", CustomerAuthHeaders);
                    ResponseEntity<String> customerIdResponse = restTemplateCustomerId.exchange("https://dev.apis.discovermarket.com/customer/v2/customers",
                            HttpMethod.POST, httpEntityCustomer, String.class);
                    JSONObject customerId = new JSONObject(customerIdResponse.getBody());
                    //CustomerId Generated!
                    String cusId = (customerId.getJSONObject("data").getString("id"));
                    //Update UserInfo with CustomerId
                    userInfo.getJSONObject(user).put("CustomerId", customerId.getJSONObject("data").getString("id"));
                    System.out.println(userInfo);

                    //GetProposalId
                    ProposalAuthHeaders.setBearerAuth(tok);
                    HttpEntity<String> httpEntityProposal = new HttpEntity<>("{\"name\":\"Proposal-07-July-2022-0334\"," +
                            "\"customerPartyId\":" +
                            "\"" +
                            cusId +
                            "\"," +
                            "\"lobId\":\"61babd043571dd6f65eef3d6\",\"taxRate\":10}", ProposalAuthHeaders); //By right lobId is CyberQuote LOB
                    ResponseEntity<String> proposalIdResponse = restTemplateProposalId.exchange("https://dev.apis.discovermarket.com/proposal/v2/proposals",
                            HttpMethod.POST, httpEntityProposal, String.class);

                    JSONObject proposalId = new JSONObject(proposalIdResponse.getBody());
                    //ProposalId Generated!
                    String propId = proposalId.getJSONObject("data").getString("id");
                    //lobId
                    String lobId = proposalId.getJSONObject("data").getString("lobId");
                    System.out.println("-----------");
                    System.out.println(propId);
                    System.out.println("-----------");
                    System.out.println(lobId);
                    //Update userInfo with ProposalId
                    userInfo.getJSONObject(user).put("ProposalId", proposalId.getJSONObject("data").getString("id"));
                    System.out.println("-----------");
                    System.out.println(userInfo);
                    System.out.println("-----------");
                    //TODO FullUpdate
                    FullUpdateAuthHeaders.setBearerAuth(tok); //could jolly well be another token
                    HttpEntity<String> httpEntityFullUpdate = new HttpEntity<>("{\n" +
                            "   \"currentStep\":\"fact-finding\",\n" +
                            "   \"effectiveDate\":\"2022-07-07T09:21:01.834+00:00\",\n" +
                            "   \"expiryDate\":null,\n" +
                            "   \"filterSelection\":{\n" +
                            "      \"toIsoCurrencyCode\":\"USD\",\n" +
                            "      \"fromIsoCurrencyCode\":\"SGD\",\n" +
                            "      \"exchangeRate\":null,\n" +
                            "      \"areaOfCoverageCode\":null,\n" +
                            "      \"minPrice\":null,\n" +
                            "      \"membersSelection\":[\n" +
                            "         \"62c6a57d1f69c43fecb629f8\"\n" +
                            "      ],\n" +
                            "      \"maxPrice\":null,\n" +
                            "      \"coverSelections\":[\n" +
                            "         {\n" +
                            "            \"typeCode\":\"COT0000001\",\n" +
                            "            \"typeDescription\":\"inpatient_available\",\n" +
                            "            \"isSelected\":true\n" +
                            "         },\n" +
                            "         {\n" +
                            "            \"typeCode\":\"COT0000002\",\n" +
                            "            \"typeDescription\":\"outpatient_available\",\n" +
                            "            \"isSelected\":false\n" +
                            "         },\n" +
                            "         {\n" +
                            "            \"typeCode\":\"COT0000003\",\n" +
                            "            \"typeDescription\":\"dental_available\",\n" +
                            "            \"isSelected\":false\n" +
                            "         },\n" +
                            "         {\n" +
                            "            \"typeCode\":\"COT0000004\",\n" +
                            "            \"typeDescription\":\"maternity_available\",\n" +
                            "            \"isSelected\":false\n" +
                            "         },\n" +
                            "         {\n" +
                            "            \"typeCode\":\"COT0000005\",\n" +
                            "            \"typeDescription\":\"vision_available\",\n" +
                            "            \"isSelected\":false\n" +
                            "         },\n" +
                            "         {\n" +
                            "            \"typeCode\":\"COT0000006\",\n" +
                            "            \"typeDescription\":\"wellness_available\",\n" +
                            "            \"isSelected\":false\n" +
                            "         },\n" +
                            "         {\n" +
                            "            \"typeCode\":\"COT0000020\",\n" +
                            "            \"typeDescription\":\"repatriation_and_evacuation_available\",\n" +
                            "            \"isSelected\":false\n" +
                            "         }\n" +
                            "      ],\n" +
                            "      \"areaOfCoverageSelections\":[\n" +
                            "         {\n" +
                            "            \"code\":\"AC0000001\",\n" +
                            "            \"description\":\"worldwide_exclude_usa\",\n" +
                            "            \"isSelected\":true\n" +
                            "         },\n" +
                            "         {\n" +
                            "            \"code\":\"AC0000002\",\n" +
                            "            \"description\":\"worldwide_include_usa\",\n" +
                            "            \"isSelected\":false\n" +
                            "         },\n" +
                            "         {\n" +
                            "            \"code\":\"AC0000003\",\n" +
                            "            \"description\":\"asia_include_singapore\",\n" +
                            "            \"isSelected\":false\n" +
                            "         }\n" +
                            "      ],\n" +
                            "      \"productOptionsSelections\":null,\n" +
                            "      \"tariffOptionsSelections\":null,\n" +
                            "      \"sorting\":\"Insurer-AZ\"\n" +
                            "   },\n" +
                            "   \"name\":\"Proposal-07-July-2022-0520\",\n" +
                            "   \"note\":null,\n" +
                            "   \"exchangeRate\":1,\n" +
                            "   \"currency\":\"SGD\",\n" +
                            "   \"quotations\":[\n" +
                            "      {\n" +
                            "         \"proposalId\":\"" +
                            propId +
                            "\",\n" +
                            "         \"displayOrder\":1,\n" +
                            "         \"proposalQuotationId\":\"62c6a57f018fe342ab6f8365\",\n" +
                            "         \"productId\":\"620db4d8930b8e4c589482b7\",\n" +
                            "         \"quotationDetails\":[\n" +
                            "            {\n" +
                            "               \"partyId\":\"62c6a57d1f69c43fecb629f8\",\n" +
                            "               \"lastSyncTimeStamp\":\"2022-07-07T09:21:03.671Z\",\n" +
                            "               \"externalSourceId\":null,\n" +
                            "               \"mobileNumber\":\"\",\n" +
                            "               \"nationalityIsoCountryCode\":\"\",\n" +
                            "               \"email\":\"\",\n" +
                            "               \"countryResidenceIsoCountryCode\":\"\",\n" +
                            "               \"customerUserId\":\"" +
                            cusId +
                            "\",\n" +
                            "               \"partyDescription\":null,\n" +
                            "               \"partyName\":\"Primary Insured\",\n" +
                            "               \"partyLastName\":\"\",\n" +
                            "               \"dateOfBirth\":null,\n" +
                            "               \"dateOfDeath\":null,\n" +
                            "               \"genderCode\":\"0\",\n" +
                            "               \"maritalStatusCode\":\"NK\",\n" +
                            "               \"annualPremiumAmount\":0,\n" +
                            "               \"productSelectedList\":[\n" +
                            "                  \n" +
                            "               ],\n" +
                            "               \"partyDependents\":[\n" +
                            "                  \n" +
                            "               ]\n" +
                            "            }\n" +
                            "         ],\n" +
                            "         \"startDate\":null,\n" +
                            "         \"endDate\":\"\",\n" +
                            "         \"isSelectedForPolicy\":false,\n" +
                            "         \"taxAmount\":0,\n" +
                            "         \"totalPremium\":0\n" +
                            "      },\n" +
                            "      {\n" +
                            "         \"proposalId\":\"" +
                            propId +
                            "\",\n" +
                            "         \"displayOrder\":2,\n" +
                            "         \"proposalQuotationId\":\"62c6a57fd23dbfc853a7ef72\",\n" +
                            "         \"productId\":\"620db4e3930b8e4c589482b9\",\n" +
                            "         \"quotationDetails\":[\n" +
                            "            {\n" +
                            "               \"partyId\":\"62c6a57d1f69c43fecb629f8\",\n" +
                            "               \"lastSyncTimeStamp\":\"2022-07-07T09:21:03.671Z\",\n" +
                            "               \"externalSourceId\":null,\n" +
                            "               \"mobileNumber\":\"\",\n" +
                            "               \"nationalityIsoCountryCode\":\"\",\n" +
                            "               \"email\":\"\",\n" +
                            "               \"countryResidenceIsoCountryCode\":\"\",\n" +
                            "               \"customerUserId\":\"" +
                            cusId +
                            "\",\n" +
                            "               \"partyDescription\":null,\n" +
                            "               \"partyName\":\"Primary Insured\",\n" +
                            "               \"partyLastName\":\"\",\n" +
                            "               \"dateOfBirth\":null,\n" +
                            "               \"dateOfDeath\":null,\n" +
                            "               \"genderCode\":\"0\",\n" +
                            "               \"maritalStatusCode\":\"NK\",\n" +
                            "               \"annualPremiumAmount\":0,\n" +
                            "               \"productSelectedList\":[\n" +
                            "                  \n" +
                            "               ],\n" +
                            "               \"partyDependents\":[\n" +
                            "                  \n" +
                            "               ]\n" +
                            "            }\n" +
                            "         ],\n" +
                            "         \"startDate\":null,\n" +
                            "         \"endDate\":\"\",\n" +
                            "         \"isSelectedForPolicy\":false,\n" +
                            "         \"taxAmount\":0,\n" +
                            "         \"totalPremium\":0\n" +
                            "      },\n" +
                            "      {\n" +
                            "         \"proposalId\":\"" +
                            propId +
                            "\",\n" +
                            "         \"displayOrder\":3,\n" +
                            "         \"proposalQuotationId\":\"62c6a57f0ab7438f4aab213e\",\n" +
                            "         \"productId\":\"620db4ef930b8e4c589482bb\",\n" +
                            "         \"quotationDetails\":[\n" +
                            "            {\n" +
                            "               \"partyId\":\"62c6a57d1f69c43fecb629f8\",\n" +
                            "               \"lastSyncTimeStamp\":\"2022-07-07T09:21:03.671Z\",\n" +
                            "               \"externalSourceId\":null,\n" +
                            "               \"mobileNumber\":\"\",\n" +
                            "               \"nationalityIsoCountryCode\":\"\",\n" +
                            "               \"email\":\"\",\n" +
                            "               \"countryResidenceIsoCountryCode\":\"\",\n" +
                            "               \"customerUserId\":\"" +
                            cusId +
                            "\",\n" +
                            "               \"partyDescription\":null,\n" +
                            "               \"partyName\":\"Primary Insured\",\n" +
                            "               \"partyLastName\":\"\",\n" +
                            "               \"dateOfBirth\":null,\n" +
                            "               \"dateOfDeath\":null,\n" +
                            "               \"genderCode\":\"0\",\n" +
                            "               \"maritalStatusCode\":\"NK\",\n" +
                            "               \"annualPremiumAmount\":0,\n" +
                            "               \"productSelectedList\":[\n" +
                            "                  \n" +
                            "               ],\n" +
                            "               \"partyDependents\":[\n" +
                            "                  \n" +
                            "               ]\n" +
                            "            }\n" +
                            "         ],\n" +
                            "         \"startDate\":null,\n" +
                            "         \"endDate\":\"\",\n" +
                            "         \"isSelectedForPolicy\":false,\n" +
                            "         \"taxAmount\":0,\n" +
                            "         \"totalPremium\":0\n" +
                            "      },\n" +
                            "      {\n" +
                            "         \"proposalId\":\"" +
                            propId +
                            "\",\n" +
                            "         \"displayOrder\":4,\n" +
                            "         \"proposalQuotationId\":\"62c6a57fbde73be9bfd0be00\",\n" +
                            "         \"productId\":\"620db4f9930b8e4c589482bd\",\n" +
                            "         \"quotationDetails\":[\n" +
                            "            {\n" +
                            "               \"partyId\":\"62c6a57d1f69c43fecb629f8\",\n" +
                            "               \"lastSyncTimeStamp\":\"2022-07-07T09:21:03.671Z\",\n" +
                            "               \"externalSourceId\":null,\n" +
                            "               \"mobileNumber\":\"\",\n" +
                            "               \"nationalityIsoCountryCode\":\"\",\n" +
                            "               \"email\":\"\",\n" +
                            "               \"countryResidenceIsoCountryCode\":\"\",\n" +
                            "               \"customerUserId\":\"" +
                            cusId +
                            "\",\n" +
                            "               \"partyDescription\":null,\n" +
                            "               \"partyName\":\"Primary Insured\",\n" +
                            "               \"partyLastName\":\"\",\n" +
                            "               \"dateOfBirth\":null,\n" +
                            "               \"dateOfDeath\":null,\n" +
                            "               \"genderCode\":\"0\",\n" +
                            "               \"maritalStatusCode\":\"NK\",\n" +
                            "               \"annualPremiumAmount\":0,\n" +
                            "               \"productSelectedList\":[\n" +
                            "                  \n" +
                            "               ],\n" +
                            "               \"partyDependents\":[\n" +
                            "                  \n" +
                            "               ]\n" +
                            "            }\n" +
                            "         ],\n" +
                            "         \"startDate\":null,\n" +
                            "         \"endDate\":\"\",\n" +
                            "         \"isSelectedForPolicy\":false,\n" +
                            "         \"taxAmount\":0,\n" +
                            "         \"totalPremium\":0\n" +
                            "      }\n" +
                            "   ],\n" +
                            "   \"baseBundleId\":\"620db4ca930b8e4c589482b5\"\n" +
                            "}", FullUpdateAuthHeaders);

                    ResponseEntity<String> fullUpdateResponse = restTemplateFullUpdate.exchange("https://dev.apis.discovermarket.com/proposal/v2/proposals/" +
                                    propId +
                                    "/full-update", HttpMethod.PUT, httpEntityFullUpdate, String.class);
                    JSONObject fullUp = new JSONObject(fullUpdateResponse.getBody());
                    System.out.println(fullUp);
                    System.out.println("-----------");


                } catch(HttpClientErrorException e){
                    System.out.println("Not working client error");

                } catch (HttpServerErrorException e){
                    System.out.println("Not working");
                    break;
                }


                boolean BA = intent.equals("BA");

                // Update user percentage and input data
                userInfo.getJSONObject(user).put(intent, userInfo.getJSONObject(user).getInt(intent) + percentage);
                userInfo.getJSONObject(user).put(BA ? "BizActs" : "Exp", userInfo.getJSONObject(user).getString(BA ? "BizActs" : "Exp")
                        + params.getString(intent) + ": " + params.getString("percentage") + "\n");

                // Validation of input data total business activity or exposure adds up to 100%
                if (userInfo.getJSONObject(user).getInt(intent) == 100) {

                    fulfillment.put("outputContexts", BA ? turnoverContext : nameContext);
                    fulfillment.put("fulfillmentText", BA ? "What is your company's annual turnover?" : "Great, thanks! Now we just need to know a bit more about you. What is your name?");
                    userInfo.put(user, new JSONObject("{\"BA\":0,\"Exposure\":0, \"BizActs\":\"\", \"Exp\":\"\"}"));
                } else {

                    fulfillment.put("outputContexts", BA ? quoteContext : exposureContext);
                    if (userInfo.getJSONObject(user).getInt(intent) < 100) {

                        fulfillment.put("fulfillmentText", BA ? "What are your other business activities? (total business activity must add up to 100%)\n\n" + userInfo.getJSONObject(user).getString("BizActs") :
                                "What is your other market exposure? (total exposure must add up to 100%)\n\n" + userInfo.getJSONObject(user).getString("Exp"));
                    } else {

                        fulfillment.put("fulfillmentText", BA ? "Total business activity cannot be more than 100%, please state your business activities again" :
                                "Total exposure cannot be more than 100%, please state your exposure again");
                        userInfo.put(user, new JSONObject("{\"BA\":0,\"Exposure\":0, \"BizActs\":\"\", \"Exp\":\"\"}"));
                    }
                }
                break;
            case "quote-start":
            case "Welcome - yes":
            case "reset":

                // Reset the list of excluded business activities so that it is refreshed whenever the conversation restarts
                excludedBizActivities = new ArrayList<>();
                StringBuilder BizActs = new StringBuilder("Please enter your business activity and its corresponding percentage (eg. Retail: 20%/ B0001: 30%):\n\n");

                HttpEntity<String> httpEntity = new HttpEntity<>("", headers);

                // Retrieve the risk details from the DCM risk details API
                HttpStatus code;
                ResponseEntity<String> response;
                try {
                    System.out.println("***********");
                    response = restTemplate.exchange("https://product-service-dev.discovermarket.com/v2/riskdetailinfos/619c9d2e4b0253465a797fd1/620db4ca930b8e4c589482b5",
                            HttpMethod.GET, httpEntity, String.class);
                    code = response.getStatusCode();
                    System.out.println(code);

                } catch (HttpClientErrorException e)  {
                    System.out.println("--Setting new token--");
                    String token = tokenManagement.tokenization();
                    headers.setBearerAuth(token);
                    response = restTemplate.exchange("https://product-service-dev.discovermarket.com/v2/riskdetailinfos/619c9d2e4b0253465a797fd1/620db4ca930b8e4c589482b5",
                            HttpMethod.GET, httpEntity, String.class);
                    code = response.getStatusCode();
                    System.out.println(code);
                } catch (HttpServerErrorException e){
                    System.out.println("Not working");
                    break;
                }
                JSONObject riskDetails = new JSONObject(response.getBody());
                JSONArray activitiesList = riskDetails.getJSONObject("data").getJSONObject("customerCategory").getJSONArray("objectTypes").getJSONObject(0).getJSONArray("riskDetailDataGroups").getJSONObject(0).getJSONArray("dataDetailAttributes").getJSONObject(0).getJSONArray("options");
                // To generate the String of business activities to display to user, as well as to update the list of excluded business activites
                for (int i = 1; i < activitiesList.length(); i++) {
                    JSONObject activity = activitiesList.getJSONObject(i);
                    BizActs.append(activity.getString("key")).append(": ").append(activity.getString("value")).append("\n");
                    if (activity.getString("category").equals("excluded")) {

                        excludedBizActivities.add(activity.getString("value"));
                    }
                }

                fulfillment.put("outputContexts", quoteContext);
                fulfillment.put("fulfillmentText", BizActs.toString());

                break;
            case "ET":
                int turnover = params.getJSONObject("unit-currency").getInt("amount");

                if (turnover <= 0) {
                    fulfillment.put("outputContexts", turnoverContext);
                    fulfillment.put("fulfillmentText", "Annual turnover must be a positive non-zero number, please re-enter");
                }
                break;
        }
        return fulfillment;
    }
}
