/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package io.openshift.booster.catalog;

import java.beans.Transient;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * A quickstart representation
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public class Booster
{
   private String id;
   private String githubRepo;
   private String gitRef;
   private String buildProfile;
   private String description = "No description available";
   private String boosterDescriptorPath = ".openshiftio/booster.yaml";
   private Mission mission;
   private Runtime runtime;
   private Version version;

   private Path contentPath;

   private Map<String, Object> metadata = Collections.emptyMap();
   private Map<String, String> versions = new HashMap<String, String>();
   
   public String getName()
   {
      return Objects.toString(getMetadata().get("name"), getId());
   }

   public String getDescription()
   {
      return Objects.toString(getMetadata().get("description"), description);
   }

   /**
    * @return the boosterDescriptionPath
    */
   public String getBoosterDescriptionPath()
   {
      return Objects.toString(getMetadata().get("descriptionPath"), ".openshiftio/description.adoc");
   }

   /**
    * Returns the human-readable name for the given runtime version id.
    * If no matching id was found the id itself is returned.
    */
   public String runtimeVersionName(String runtimeVersionId)
   {
      return versions.getOrDefault(runtimeVersionId, runtimeVersionId);
   }

   /**
    * @return the id
    */
   public String getId()
   {
      return id;
   }

   /**
    * @return the githubRepo
    */
   public String getGithubRepo()
   {
      return githubRepo;
   }

   /**
    * @return the gitRef
    */
   public String getGitRef()
   {
      return gitRef;
   }

   /**
    * @return the buildProfile
    */
   public String getBuildProfile()
   {
      return buildProfile;
   }

   /**
    * @return the boosterDescriptorPath
    */
   @Transient
   public String getBoosterDescriptorPath()
   {
      return boosterDescriptorPath;
   }

   /**
    * @param id the id to set
    */
   public void setId(String id)
   {
      this.id = id;
   }

   /**
    * @param githubRepo the githubRepo to set
    */
   public void setGithubRepo(String githubRepo)
   {
      this.githubRepo = githubRepo;
   }

   /**
    * @param gitRef the gitRef to set
    */
   public void setGitRef(String gitRef)
   {
      this.gitRef = gitRef;
   }

   /**
    * @param buildProfile the buildProfile to set
    */
   public void setBuildProfile(String buildProfile)
   {
      this.buildProfile = buildProfile;
   }

   /**
    * @param boosterDescriptorPath the obsidianDescriptorPath to set
    */
   public void setBoosterDescriptorPath(String boosterDescriptorPath)
   {
      this.boosterDescriptorPath = boosterDescriptorPath;
   }

   /**
    * @param description the description to set
    */
   public void setDescription(String description)
   {
      this.description = description;
   }

   /**
    * @return the mission
    */
   public Mission getMission()
   {
      return mission;
   }

   /**
    * @param mission the mission to set
    */
   public void setMission(Mission mission)
   {
      this.mission = mission;
   }

   /**
    * @return the runtime
    */
   public Runtime getRuntime()
   {
      return runtime;
   }

   /**
    * @param runtime the runtime to set
    */
   public void setRuntime(Runtime runtime)
   {
      this.runtime = runtime;
   }

   /**
    * @return the version
    */
   public Version getVersion()
   {
      return version;
   }

   /**
    * @param version the version to set
    */
   public void setVersion(Version version)
   {
      this.version = version;
   }

   /**
    * @return the contentPath
    */
   @Transient
   public Path getContentPath()
   {
      return contentPath;
   }

   /**
    * @param contentPath the contentPath to set
    */
   public void setContentPath(Path contentPath)
   {
      this.contentPath = contentPath;
   }

   /**
    * @return the metadata
    */
   public Map<String, Object> getMetadata()
   {
      return metadata;
   }

   /**
    * @param metadata the metadata to set
    */
   public void setMetadata(Map<String, Object> metadata)
   {
      this.metadata = metadata;
      
      // Extract any runtime versions from the meta data and convert them
      // into a format that's easier to use
      @SuppressWarnings("unchecked")
      List<Map<String, Object>> list = (List<Map<String, Object>>) getMetadata().get("versions");
      if (list != null) {
          list.forEach((m) -> {
              versions.put(Objects.toString(m.get("id")), Objects.toString(m.get("name")));
          });
      }
   }

   @Override
   public int hashCode()
   {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((buildProfile == null) ? 0 : buildProfile.hashCode());
      result = prime * result + ((gitRef == null) ? 0 : gitRef.hashCode());
      result = prime * result + ((githubRepo == null) ? 0 : githubRepo.hashCode());
      result = prime * result + ((id == null) ? 0 : id.hashCode());
      result = prime * result + ((boosterDescriptorPath == null) ? 0 : boosterDescriptorPath.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj)
   {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      Booster other = (Booster) obj;
      if (buildProfile == null)
      {
         if (other.buildProfile != null)
            return false;
      }
      else if (!buildProfile.equals(other.buildProfile))
         return false;
      if (gitRef == null)
      {
         if (other.gitRef != null)
            return false;
      }
      else if (!gitRef.equals(other.gitRef))
         return false;
      if (githubRepo == null)
      {
         if (other.githubRepo != null)
            return false;
      }
      else if (!githubRepo.equals(other.githubRepo))
         return false;
      if (id == null)
      {
         if (other.id != null)
            return false;
      }
      else if (!id.equals(other.id))
         return false;
      if (boosterDescriptorPath == null)
      {
         if (other.boosterDescriptorPath != null)
            return false;
      }
      else if (!boosterDescriptorPath.equals(other.boosterDescriptorPath))
         return false;
      return true;
   }

   @Override
   public String toString()
   {
      return "Booster [githubRepo=" + githubRepo + ", gitRef=" + gitRef + ", buildProfile=" + buildProfile
               + ", obsidianDescriptorPath=" + boosterDescriptorPath + ", metadata=" + metadata + ", getName()="
               + getName() + ", getDescription()=" + getDescription() + "]";
   }
}
