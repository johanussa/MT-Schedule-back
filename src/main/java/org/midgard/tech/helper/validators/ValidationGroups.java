package org.midgard.tech.helper.validators;

import jakarta.validation.groups.Default;

public interface ValidationGroups {

    interface Post_Get {}
    interface Post extends Default {}
    interface Put extends Default {}
}
