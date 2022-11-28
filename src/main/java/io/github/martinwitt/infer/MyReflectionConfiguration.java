package io.github.martinwitt.infer;

import com.contrastsecurity.sarif.Address;
import com.contrastsecurity.sarif.Artifact;
import com.contrastsecurity.sarif.ArtifactChange;
import com.contrastsecurity.sarif.ArtifactContent;
import com.contrastsecurity.sarif.ArtifactLocation;
import com.contrastsecurity.sarif.Attachment;
import com.contrastsecurity.sarif.CodeFlow;
import com.contrastsecurity.sarif.ConfigurationOverride;
import com.contrastsecurity.sarif.Content;
import com.contrastsecurity.sarif.Conversion;
import com.contrastsecurity.sarif.Edge;
import com.contrastsecurity.sarif.EdgeTraversal;
import com.contrastsecurity.sarif.EnvironmentVariables;
import com.contrastsecurity.sarif.Exception;
import com.contrastsecurity.sarif.ExternalProperties;
import com.contrastsecurity.sarif.ExternalPropertyFileReference;
import com.contrastsecurity.sarif.ExternalPropertyFileReferences;
import com.contrastsecurity.sarif.FinalState;
import com.contrastsecurity.sarif.Fingerprints;
import com.contrastsecurity.sarif.Fix;
import com.contrastsecurity.sarif.GlobalMessageStrings;
import com.contrastsecurity.sarif.Graph;
import com.contrastsecurity.sarif.GraphTraversal;
import com.contrastsecurity.sarif.Hashes;
import com.contrastsecurity.sarif.Headers;
import com.contrastsecurity.sarif.Headers__1;
import com.contrastsecurity.sarif.ImmutableState;
import com.contrastsecurity.sarif.ImmutableState__1;
import com.contrastsecurity.sarif.InitialState;
import com.contrastsecurity.sarif.InitialState__1;
import com.contrastsecurity.sarif.Invocation;
import com.contrastsecurity.sarif.Location;
import com.contrastsecurity.sarif.LocationRelationship;
import com.contrastsecurity.sarif.LogicalLocation;
import com.contrastsecurity.sarif.Message;
import com.contrastsecurity.sarif.MessageStrings;
import com.contrastsecurity.sarif.MultiformatMessageString;
import com.contrastsecurity.sarif.Node;
import com.contrastsecurity.sarif.Notification;
import com.contrastsecurity.sarif.OriginalUriBaseIds;
import com.contrastsecurity.sarif.Parameters;
import com.contrastsecurity.sarif.PartialFingerprints;
import com.contrastsecurity.sarif.PhysicalLocation;
import com.contrastsecurity.sarif.PropertyBag;
import com.contrastsecurity.sarif.Rectangle;
import com.contrastsecurity.sarif.Region;
import com.contrastsecurity.sarif.Replacement;
import com.contrastsecurity.sarif.ReportingConfiguration;
import com.contrastsecurity.sarif.ReportingDescriptor;
import com.contrastsecurity.sarif.ReportingDescriptorReference;
import com.contrastsecurity.sarif.ReportingDescriptorRelationship;
import com.contrastsecurity.sarif.Result;
import com.contrastsecurity.sarif.ResultProvenance;
import com.contrastsecurity.sarif.Role;
import com.contrastsecurity.sarif.Run;
import com.contrastsecurity.sarif.RunAutomationDetails;
import com.contrastsecurity.sarif.SarifSchema210;
import com.contrastsecurity.sarif.SpecialLocations;
import com.contrastsecurity.sarif.Stack;
import com.contrastsecurity.sarif.StackFrame;
import com.contrastsecurity.sarif.State;
import com.contrastsecurity.sarif.Suppression;
import com.contrastsecurity.sarif.ThreadFlow;
import com.contrastsecurity.sarif.ThreadFlowLocation;
import com.contrastsecurity.sarif.Tool;
import com.contrastsecurity.sarif.ToolComponent;
import com.contrastsecurity.sarif.ToolComponentReference;
import com.contrastsecurity.sarif.TranslationMetadata;
import com.contrastsecurity.sarif.VersionControlDetails;
import com.contrastsecurity.sarif.WebRequest;
import com.contrastsecurity.sarif.WebResponse;
import io.quarkus.runtime.annotations.RegisterForReflection;

/**
 * Here we register all the classes that are used in the SARIF schema.
 */
@RegisterForReflection(
        targets = {
            Address.class,
            Artifact.class,
            ArtifactChange.class,
            ArtifactContent.class,
            ArtifactLocation.class,
            Attachment.class,
            CodeFlow.class,
            ConfigurationOverride.class,
            Content.class,
            Conversion.class,
            Edge.class,
            EdgeTraversal.class,
            EnvironmentVariables.class,
            Exception.class,
            ExternalProperties.class,
            ExternalPropertyFileReference.class,
            ExternalPropertyFileReferences.class,
            FinalState.class,
            Fingerprints.class,
            Fix.class,
            GlobalMessageStrings.class,
            Graph.class,
            GraphTraversal.class,
            Hashes.class,
            Headers.class,
            Headers__1.class,
            ImmutableState.class,
            ImmutableState__1.class,
            InitialState.class,
            InitialState__1.class,
            Invocation.class,
            Location.class,
            LocationRelationship.class,
            LogicalLocation.class,
            Message.class,
            MessageStrings.class,
            MultiformatMessageString.class,
            Node.class,
            Notification.class,
            OriginalUriBaseIds.class,
            Parameters.class,
            PartialFingerprints.class,
            PhysicalLocation.class,
            PropertyBag.class,
            Rectangle.class,
            Region.class,
            Replacement.class,
            ReportingConfiguration.class,
            ReportingDescriptor.class,
            ReportingDescriptorReference.class,
            ReportingDescriptorRelationship.class,
            Result.class,
            ResultProvenance.class,
            Role.class,
            Run.class,
            RunAutomationDetails.class,
            SarifSchema210.class,
            SpecialLocations.class,
            Stack.class,
            StackFrame.class,
            State.class,
            Suppression.class,
            ThreadFlow.class,
            ThreadFlowLocation.class,
            Tool.class,
            ToolComponent.class,
            ToolComponentReference.class,
            TranslationMetadata.class,
            VersionControlDetails.class,
            WebRequest.class,
            WebResponse.class,
            SarifSchema210.Version.class,
            com.contrastsecurity.sarif.Result.Level.class
        })
public class MyReflectionConfiguration {}
