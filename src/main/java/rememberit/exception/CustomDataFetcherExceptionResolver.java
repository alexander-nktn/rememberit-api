package rememberit.exception;

import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.schema.DataFetchingEnvironment;
import org.springframework.graphql.execution.DataFetcherExceptionResolverAdapter;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class CustomDataFetcherExceptionResolver extends DataFetcherExceptionResolverAdapter {

    @Override
    protected GraphQLError resolveToSingleError(
            Throwable ex,
            DataFetchingEnvironment env
    ) {
        if (ex instanceof ApplicationException appEx) {
            Map<String, Object> extensions = new HashMap<>();
            extensions.put("errorCode", appEx.getErrorCode());
            extensions.put("context", appEx.getContext() != null ? appEx.getContext().getProperties() : null);
            extensions.put("rootCause", appEx.getRootCauseMessage());

            return GraphqlErrorBuilder.newError(env)
                    .message(appEx.getMessage())
                    .errorType(ErrorType.INTERNAL_ERROR)
                    .extensions(extensions)
                    .build();
        }

        return null;
    }
}