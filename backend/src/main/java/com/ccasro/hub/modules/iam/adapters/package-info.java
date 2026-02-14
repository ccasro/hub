/**
 * Adapters layer for the IAM module. This layer contains implementations that connect the
 * application core to the outside world following the Ports and Adapters (Hexagonal Architecture)
 * pattern. Inbound adapters expose application use cases to external clients (e.g., REST
 * controllers, messaging listeners). Outbound adapters implement domain ports to integrate with
 * external systems such as databases, identity providers, and third-party services.
 */
package com.ccasro.hub.modules.iam.adapters;
