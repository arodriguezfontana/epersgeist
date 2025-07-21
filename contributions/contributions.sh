#!/bin/bash

# Verificar que se pase al menos la branch
if [ -z "$1" ]; then
  echo "⚠️  Debes especificar la rama como primer parámetro."
  echo "Uso: ./stats_defaults.sh <branch> [sinceDate]"
  exit 1
fi

branch="$1"
sinceDate="${2:-$(date -d '2 weeks ago' +%Y-%m-%d)}"

git pull

# Cambiar de rama (silencioso)
git checkout "$branch" >/dev/null 2>&1 || {
  echo "❌ No se pudo cambiar a la rama '$branch'"
  exit 1
}

# Obtener autores únicos
mapfile -t authors < <(git log "$branch" --since="$sinceDate" --pretty="%an" | sort -u)

# Archivo de salida
timestamp=$(date +%Y%m%d_%H%M%S)
outputFile="stats_${timestamp}.json"
: > "$outputFile"  # Limpia archivo

# Inicio del JSON
{
  echo "{"
  echo "  \"Contributions-per-user\": ["
} >> "$outputFile"

merged_branches=()
first=1

for author in "${authors[@]}"; do
  mapfile -t messages < <(git log "$branch" --since="$sinceDate" --author="$author" --pretty="%s")
  commits=${#messages[@]}

  stats=$(git log "$branch" --since="$sinceDate" --author="$author" --pretty=tformat: --numstat |
    awk '{ add += $1; del += $2 } END { print add+0, del+0 }')

  added=$(echo "$stats" | cut -d' ' -f1)
  deleted=$(echo "$stats" | cut -d' ' -f2)

  # Manejo de coma para múltiples autores
  [ $first -eq 0 ] && echo "," >> "$outputFile"

  echo "    {" >> "$outputFile"
  echo "      \"👤 Contributor\": \"${author}\"," >> "$outputFile"
  echo "      \"📊 Stats\": {" >> "$outputFile"
  echo "        \"📨 commits\": $commits," >> "$outputFile"
  echo "        \"✅ added\": $added," >> "$outputFile"
  echo "        \"⛔ deleted\": $deleted" >> "$outputFile"
  echo "      }," >> "$outputFile"
  echo "      \"💬 Messages\": [" >> "$outputFile"

  valid_msgs=()

  for msg in "${messages[@]}"; do
    msg_escaped="${msg//\"/\\\"}"

    if [[ "$msg_escaped" =~ ^Merge\ pull\ request\ \#([0-9]+)\ from\ (.+)$ ]]; then
      pr_number="${BASH_REMATCH[1]}"
      pr_branch="${BASH_REMATCH[2]}"
      merged_branches+=("$pr_number|$pr_branch")
    elif [[ -n "$msg_escaped" ]]; then
      valid_msgs+=("\"$msg_escaped\"")
    fi
  done

  for i in "${!valid_msgs[@]}"; do
    echo -n "        ${valid_msgs[$i]}" >> "$outputFile"
    [ "$i" -lt $(( ${#valid_msgs[@]} - 1 )) ] && echo "," >> "$outputFile" || echo >> "$outputFile"
  done

  echo "      ]" >> "$outputFile"
  echo -n "    }" >> "$outputFile"

  first=0
done

echo "" >> "$outputFile"
echo "  ]," >> "$outputFile"

# Commits directos a la rama
echo "  \"❌ Commits directos a $branch\": [" >> "$outputFile"
first_direct=1

mapfile -t direct_commits < <(git log "$branch" --first-parent --since="$sinceDate" --pretty=format:"%s|%an" | grep -v "^Merge pull request")

for entry in "${direct_commits[@]}"; do
  commit_msg="${entry%|*}"
  author="${entry##*|}"

  # Escape
  commit_msg="${commit_msg//\"/\\\"}"
  author="${author//\"/\\\"}"

  [ $first_direct -eq 0 ] && echo "," >> "$outputFile"

  echo "    {" >> "$outputFile"
  echo "      \"commit\": \"$commit_msg\"," >> "$outputFile"
  echo "      \"author\": \"$author\"" >> "$outputFile"
  echo -n "    }" >> "$outputFile"

  first_direct=0
done

# Cierre del JSON
{
  echo ""
  echo "  ]"
  echo "}"
} >> "$outputFile"

echo "✅ Métricas calculadas. Archivo generado: $outputFile"
