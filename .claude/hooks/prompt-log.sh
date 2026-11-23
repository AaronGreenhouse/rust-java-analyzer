#!/bin/bash
# Appends each Claude Code prompt and Claude's final reply to claude-prompt-log.md.
# Run by the UserPromptSubmit and Stop hooks in .claude/settings.local.json, which
# pass the event as JSON on stdin.
set -euo pipefail

input=$(cat)
dir="${CLAUDE_PROJECT_DIR:-$(pwd)}"
log="$dir/claude-prompt-log.md"
session=$(jq -r '.session_id // "unknown"' <<<"$input")
# Marks that a prompt was logged and its reply has not been yet.
pending="${TMPDIR:-/tmp}/claude-prompt-log-$session.pending"

case "$(jq -r '.hook_event_name' <<<"$input")" in
UserPromptSubmit)
    {
        printf '\n## %s\n\n### Prompt\n\n' "$(date '+%Y-%m-%d %H:%M %Z')"
        jq -r '.prompt' <<<"$input"
    } >>"$log"
    touch "$pending"
    ;;
Stop)
    # Only log a reply to a prompt that was logged, so one turn is never logged twice.
    [ -f "$pending" ] || exit 0
    rm -f "$pending"
    reply=$(jq -r '.last_assistant_message // empty' <<<"$input")
    if [ -z "$reply" ]; then
        # Fall back to the last assistant message with text in the transcript.
        transcript=$(jq -r '.transcript_path // empty' <<<"$input")
        if [ -n "$transcript" ] && [ -f "$transcript" ]; then
            reply=$(jq -rs '[.[] | select(.type == "assistant")
                | [.message.content[]? | select(.type == "text") | .text]
                | select(length > 0) | join("\n\n")] | last // empty' "$transcript")
        fi
    fi
    printf '\n### Response\n\n%s\n' "${reply:-(no reply text found)}" >>"$log"
    ;;
esac
